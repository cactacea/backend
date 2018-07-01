package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future

trait SocialAccountsService {

  def getService(providerId: String): Future[AuthenticationService]

}
