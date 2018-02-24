package io.github.cactacea.core.infrastructure.clients

import com.twitter.util.Future

trait SocialClient {

  def get(accessTokenKey: String, accessTokenSecret: String): Future[Option[SocialAccount]]

}