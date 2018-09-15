package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.OAuth2
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finagle.oauth2._
import com.twitter.finatra.http.Controller
import com.twitter.inject.annotations.Flag
import com.twitter.util.Future
import io.github.cactacea.backend.models.requests.oauth.GetAuthorize
import io.github.cactacea.backend.utils.oauth.{OAuthCodeGenerator, OAuthHandler, OAuthService, OAuthTokenGenerator}
import io.github.cactacea.backend.views.{ErrorView, SignInView}

@Singleton
class OAuthController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String) extends Controller with OAuth2 with OAuthTokenInJson with OAuthErrorInJson {

  protected val tagName = "OAuth"

  @Inject private var oauthService: OAuthService = _
  @Inject private var tokenGenerator: OAuthTokenGenerator = _
  @Inject private var codeGenerator: OAuthCodeGenerator = _
  @Inject private var dataHandler: OAuthHandler = _

  private val applicationName = "Cactacea"

  prefix(apiPrefix) {

    get("/oauth2/authorization") { req: GetAuthorize =>
      oauthService.validateResponseType(req.responseType) match {
        case Right(_) =>
          oauthService.validateClient(req.clientId).map(_ match {
            case Right((_, permissions)) =>
              oauthService.validateScope(req.scope, permissions) match {
                case Right(_) =>
                  val scope = req.scope.map({ s => s"&scope=${s}"}).getOrElse("")
                  val location = s"/oauth2/authentication?response_type=${req.responseType}&client_id=${req.clientId}${scope}"
                  response.status(Status.SeeOther).location(location)
                case Left(e) =>
                  handleError(e)
              }
            case Left(e) =>
              handleError(e)
          })
        case Left(e) =>
          handleError(e)
      }
    }

    get("/oauth2/authentication") { req: GetAuthorize =>
      oauthService.validateClient(req.clientId).map(_ match {
        case Right((client, permissions)) =>
          oauthService.validateScope(req.scope, permissions) match {
            case Right(permissions) =>
              (req.username, req.password) match {
                case (Some(username), Some(password)) =>
                  oauthService.signIn(username, password).map(_ match {
                    case Some(a) =>
                      req.responseType match {
                        case "code" =>
                          val scope = req.scope.mkString(",")
                          val code = codeGenerator.generateCode(a.id.value, req.clientId, scope)
                          val location = client.redirectUri + s"?code=${code}"
                          response.status(Status.SeeOther).location(location)
                        case "token" =>
                          val scope = req.scope.mkString(",")
                          val token = tokenGenerator.generateToken(a.id.value, req.clientId, scope)
                          val location = client.redirectUri + s"?access_token=${token}&token_type=Bearer"
                          response.status(Status.SeeOther).location(location)
                      }
                    case None =>
                  })
                case _ =>
                  val scope = permissions.map(_.value).mkString(",")
                  SignInView(req.responseType, req.clientId, scope, None)
              }
            case Left(e) =>
              ErrorView(applicationName, e.description)
          }
        case Left(e) =>
          ErrorView(applicationName, e.description)
      })
    }

  }

  post("/oauth2/token") { req: Request =>
    issueAccessToken(req, dataHandler) flatMap { token =>
      Future(convertToken(token))
    } handle {
      case e: OAuthError => handleError(e)
    }
  }

}
