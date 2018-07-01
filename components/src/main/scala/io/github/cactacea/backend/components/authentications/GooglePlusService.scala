package io.github.cactacea.backend.components.authentications

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.AuthenticationService
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.OperationNotAllowed
import io.github.cactacea.backend.util.clients.google.GoogleClient

class GooglePlusService extends AuthenticationService {

  @Inject private var googleClient: GoogleClient = _

  val providerId: String = "google"

  def issueCode(providerKey: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

  def validateCode(providerKey: String, authenticationCode: String): Future[String] = {
    googleClient.me(providerKey).map(s => s.id)
  }

  def resetPassword(providerKey: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

}
