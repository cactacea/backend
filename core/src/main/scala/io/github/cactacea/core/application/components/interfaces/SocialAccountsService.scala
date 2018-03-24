package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future

trait SocialAccountsService {

  def getService(socialAccountType: String): Future[AuthenticationService]

}
