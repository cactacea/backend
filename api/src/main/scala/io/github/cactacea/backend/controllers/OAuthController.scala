package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.OAuth2
import com.twitter.finagle.http.Request
import com.twitter.finagle.oauth2.{OAuthError, OAuthErrorInJson, OAuthTokenInJson, RedirectUriMismatch}
import com.twitter.finatra.http.Controller
import com.twitter.util.Future
import io.github.cactacea.backend.models.requests.auth.{GetAuth, PostAuth}
import io.github.cactacea.backend.models.responses.AccessCodeCreated
import io.github.cactacea.backend.views.SignInView
import io.github.cactacea.core.domain.enums.AccountStatusType
import io.github.cactacea.core.infrastructure.dao.{AccountsDAO, AuthDAO}
import io.github.cactacea.core.util.oauth.{InvalidResponseType, OAuthHandler}
import io.github.cactacea.core.util.tokens.OAuthTokenGenerator

@Singleton
class OAuthController extends Controller with OAuth2 with OAuthTokenInJson with OAuthErrorInJson {

  @Inject private var authDAO: AuthDAO = _
  @Inject private var accountsDAO: AccountsDAO = _
  @Inject private var oAuthTokenGenerator: OAuthTokenGenerator = _
  @Inject private var dataHandler: OAuthHandler = _

  get("/auth") { req: GetAuth =>
    authDAO.validateRedirectUri(req.clientId, req.redirectUri).map(_ match {
      case true =>
        if (req.responseType == "code" || req.responseType == "token") {
          SignInView(req.responseType, req.clientId, req.redirectUri, req.scope, req.state, req.codeChallenge, req.codeChallengeMethod)
        } else {
          handleError(new InvalidResponseType())
        }
      case false =>
        handleError(new RedirectUriMismatch())
    })
  }

  post("/auth") { req: PostAuth =>
    authDAO.validateRedirectUri(req.clientId, req.redirectUri).map(_ match {
      case true =>
        accountsDAO.find(req.username, req.password).map(_ match {
          case Some(a) =>
            if (a.accountStatus == AccountStatusType.singedUp) {
              if (req.responseType == "code") {
                val code = oAuthTokenGenerator.generateCode()
                val created = AccessCodeCreated(code, None)
                response.created(created).location(req.redirectUri)

              } else if (req.responseType == "token") {
                issueAccessToken(req.request, dataHandler) flatMap { token =>
                  Future(convertToken(token))
                } handle {
                  case e: OAuthError => handleError(e)
                }
              } else {
                handleError(new InvalidResponseType())
              }
            } else {
              SignInView(req.responseType, req.clientId, req.redirectUri, req.scope, req.state, req.codeChallenge, req.codeChallengeMethod)
            }
          case None =>
            SignInView(req.responseType, req.clientId, req.redirectUri, req.scope, req.state, req.codeChallenge, req.codeChallengeMethod)
        })
      case false =>
        handleError(new RedirectUriMismatch())
    })
  }

  post("/token") {req: Request =>
    issueAccessToken(req, dataHandler) flatMap { token =>
      Future(convertToken(token))
    } handle {
      case e: OAuthError => handleError(e)
    }
  }

}
