package io.github.cactacea.oauth

import com.twitter.finagle.oauth2.UnsupportedResponseType
import com.twitter.util.Future

trait OAuth2Code {

  def issueAccessCode(responseType: String,
                      clientId: String,
                      scope: Option[String],
                      redirectUri: Option[String],
                      dataHandler: OAuthDataHandler): Future[String] = {
    responseType match {
      case "code" =>
        dataHandler.createAccessCode(clientId, scope, redirectUri)
      case _ =>
        Future.exception(new UnsupportedResponseType("Invalid response type error occurred."))
    }
  }

}
