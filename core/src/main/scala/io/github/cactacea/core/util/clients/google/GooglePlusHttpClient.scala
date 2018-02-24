package io.github.cactacea.core.infrastructure.clients.google

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.{Http, http}
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import com.typesafe.config.ConfigFactory
import io.github.cactacea.core.infrastructure.clients.{SocialAccount, SocialClient}

@Singleton
class GooglePlusHttpClient @Inject()(objectMapper: FinatraObjectMapper) extends SocialClient {

  private val config = ConfigFactory.load()
  private val apiKey = config.getString("google.apiKey")

  private val hostname = "www.googleapis.com"
  private val client = Http.client.withTls(hostname).newService(s"$hostname:443")

  override def get(accessTokenKey: String, accessTokenSecret: String): Future[Option[SocialAccount]] =  {
    val request = http.Request(http.Method.Get, s"/plus/v1/people/me?api_key=$apiKey&access_token=$accessTokenKey")
    client(request).map({ f =>
      val g = objectMapper.parse[GoogleAccount](f.contentString)
      Some(SocialAccount(g.id))
    }).rescue({
      case _: Exception =>
        Future.None
    })
  }

}
