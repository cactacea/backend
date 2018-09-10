package io.github.cactacea.backend.externals.infrastructure.authentications

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.AuthenticationService
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.OperationNotAllowed
import io.github.cactacea.backend.externals.utils.clients.twitter.TwitterClient

class TwitterService extends AuthenticationService {

  @Inject private var twitterClient: TwitterClient = _

  val providerId: String = "twitter"

  def issueCode(providerKey: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

  def validateCode(providerKey: String, authenticationCode: String): Future[String] = {
    twitterClient.me(providerKey, authenticationCode).map(s => s.id)
  }

  def resetPassword(providerKey: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

}
