package io.github.cactacea.core.infrastructure.clients.twitter

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.util.Configurations.{consumerTokenKey, consumerTokenSecret}
import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.clients.{SocialAccount, SocialClient}

@Singleton
class TwitterHttpClient extends SocialClient {

  val consumerToken = ConsumerToken(key = consumerTokenKey, secret = consumerTokenSecret)

  import scala.concurrent.ExecutionContext.Implicits.global
  import io.github.cactacea.core.util.FutureConvertor._

  override def get(accessTokenKey: String, accessTokenSecret: String): Future[Option[SocialAccount]] =  {
    val accessToken = AccessToken(accessTokenKey, accessTokenSecret)
    val client = TwitterRestClient(consumerToken, accessToken)
    client.verifyCredentials(false, false, false).map({ v =>
      Some(SocialAccount(v.data.id.toString))
    }).asTwitter.rescue({
      case _: Exception => Future.None
    })
  }

}
