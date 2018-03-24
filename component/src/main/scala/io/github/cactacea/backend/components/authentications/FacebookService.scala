package io.github.cactacea.backend.components.authentications

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.AuthenticationService
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.OperationNotAllowed
import io.github.cactacea.util.clients.facebook.FacebookClient

class FacebookService extends AuthenticationService {

  @Inject private var facebookClient: FacebookClient = _

  val socialAccountType: String = "facebook"

  def issueCode(socialAccountIdentifier: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

  def validateCode(socialAccountIdentifier: String, authenticationCode: String): Future[String] = {
    facebookClient.me(socialAccountIdentifier).map(s => s.id)
  }

  def resetPassword(socialAccountIdentifier: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

}
