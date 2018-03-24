package io.github.cactacea.backend.components.authentications

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.AuthenticationService
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaErrors.OperationNotAllowed
import io.github.cactacea.util.clients.twitter.TwitterClient

class TwitterService extends AuthenticationService {

  @Inject private var twitterClient: TwitterClient = _

  val socialAccountType: String = "twitter"

  def issueCode(socialAccountIdentifier: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

  def validateCode(socialAccountIdentifier: String, authenticationCode: String): Future[String] = {
    twitterClient.me(socialAccountIdentifier, authenticationCode).map(s => s.id)
  }

  def resetPassword(socialAccountIdentifier: String): Future[Unit] = {
    Future.exception(CactaceaException(OperationNotAllowed))
  }

}
