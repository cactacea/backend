package io.github.cactacea.util.clients.twitter

import com.google.inject.Inject
import com.twitter.util.Future

class TwitterClient @Inject()(twitterHttpClient: TwitterHttpClient) {

  def me(accessToken: String, accessTokenSecret: String): Future[Twitter] = {
    twitterHttpClient.get(accessToken, accessTokenSecret).map({r => Twitter(r.id_str)})
  }

}
