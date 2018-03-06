package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future

trait SocialAccountsService {

  def get(socialAccountType: String, accessTokenKey: String, accessTokenSecret: String): Future[String]

}
