package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.OAuth2
import com.twitter.finagle.http.Request
import com.twitter.finagle.oauth2.{OAuthError, OAuthErrorInJson, OAuthTokenInJson, RedirectUriMismatch}
import com.twitter.util.Future
import io.github.cactacea.backend.models.requests.auth.{GetAuthorizeCode, PostOAuthToken}
import io.github.cactacea.backend.models.responses.AccessCodeCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.views.SignInView
import io.github.cactacea.core.domain.enums.AccountStatusType
import io.github.cactacea.core.infrastructure.dao.{AccountsDAO, AuthDAO}
import io.github.cactacea.core.util.oauth.{InvalidResponseType, OAuthHandler}
import io.github.cactacea.core.util.tokens.OAuthTokenGenerator
import io.swagger.models.Swagger

@Singleton
class OAuthController @Inject()(s: Swagger) extends BackendController with OAuth2 with OAuthTokenInJson with OAuthErrorInJson {

  protected implicit val swagger = s

  protected val tagName = "OAuth"

  @Inject private var authDAO: AuthDAO = _
  @Inject private var accountsDAO: AccountsDAO = _
  @Inject private var oAuthTokenGenerator: OAuthTokenGenerator = _
  @Inject private var dataHandler: OAuthHandler = _

  getWithDoc("/oauth2/authorize") { o =>
    o.summary("OAuth for authorization")
      .tag("OAuth2")

  } { req: GetAuthorizeCode =>
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

  postWithDoc("/oauth2/authorize") { o =>
    o.summary("OAuth for authorization confirm")
      .tag("OAuth2")

  } { req: PostOAuthToken =>
    authDAO.validateRedirectUri(req.clientId, req.redirectUri).map(_ match {
      case true =>
        accountsDAO.find(req.username, req.password).map(_ match {
          case Some(a) =>
            if (a.accountStatus == AccountStatusType.normally) {
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

  postWithDoc("/oauth2/token") { o =>
    o.summary("OAuth for token requests")
      .tag("OAuth2")

  } {req: Request =>
    issueAccessToken(req, dataHandler) flatMap { token =>
      Future(convertToken(token))
    } handle {
      case e: OAuthError => handleError(e)
    }
  }

}
