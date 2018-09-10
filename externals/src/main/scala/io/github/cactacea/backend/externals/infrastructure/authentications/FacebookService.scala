package io.github.cactacea.backend.externals.infrastructure.authentications

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.AuthenticationService
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.OperationNotAllowed
import io.github.cactacea.backend.externals.utils.clients.facebook.FacebookClient

class FacebookService extends AuthenticationService {

  @Inject private var facebookClient: FacebookClient = _

  val providerId: String = "facebook"

  def issueCode(providerKey: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

  def validateCode(providerKey: String, authenticationCode: String): Future[String] = {
    facebookClient.me(providerKey).map(s => s.id)
  }

  def resetPassword(providerKey: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

}
