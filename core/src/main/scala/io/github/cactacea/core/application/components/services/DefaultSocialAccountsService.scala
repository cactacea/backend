package io.github.cactacea.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{AuthenticationService, SocialAccountsService}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaErrors.SocialAccountNotFound

class DefaultSocialAccountsService extends SocialAccountsService {

  val services = Map[String, AuthenticationService]()

  def getService(socialAccountType: String): Future[AuthenticationService] = {
    services.get(socialAccountType) match {
      case Some(s) =>
        Future.value(s)
      case None =>
        Future.exception(CactaceaException(SocialAccountNotFound))
    }
  }

}
