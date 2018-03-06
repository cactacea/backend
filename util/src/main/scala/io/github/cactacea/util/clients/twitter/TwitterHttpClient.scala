package io.github.cactacea.util.clients.twitter

import scala.concurrent.ExecutionContext.Implicits.global
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, User}
import com.danielasfregola.twitter4s.util.Configurations.{consumerTokenKey, consumerTokenSecret}
import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.util.FutureConvertor._

@Singleton
class TwitterHttpClient  {

  // Define TWITTER_ACCESS_TOKEN_KEY and TWITTER_ACCESS_TOKEN_SECRET
  val consumerToken = ConsumerToken(key = consumerTokenKey, secret = consumerTokenSecret)

  def get(accessTokenKey: String, accessTokenSecret: String): Future[User] =  {
    val accessToken = AccessToken(accessTokenKey, accessTokenSecret)
    val client = TwitterRestClient(consumerToken, accessToken)
    client.verifyCredentials(false, false, false).asTwitter.map({ f =>
      f.data
    })
  }

}
