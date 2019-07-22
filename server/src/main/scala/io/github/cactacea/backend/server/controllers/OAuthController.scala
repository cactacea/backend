package io.github.cactacea.backend.server.controllers

import com.google.inject.Inject
import com.twitter.finagle.OAuth2
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finagle.oauth2.OAuthError
import com.twitter.finatra.http.Controller
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.server.models.requests.oauth.GetAuthorize
import io.github.cactacea.oauth.{OAuth2Code, OAuthDataHandler}

class OAuthController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, dataHandler: OAuthDataHandler)
  extends Controller with OAuth2 with OAuth2Code {

  get("/oauth2/authorization") { req: GetAuthorize =>
    issueAccessCode(
      req.responseType,
      req.clientId,
      req.scope,
      req.redirectUri,
      dataHandler).map { code =>
      val location = s"/oauth2/sign?code=${code}"
      response.status(Status.SeeOther).location(location)
    } handle {
      case e: OAuthError => e.toResponse
    }
  }

  post("/oauth2/token") {req: Request =>
    issueAccessToken(req, dataHandler) handle {
      case e: OAuthError => e.toResponse
    }
  }

}