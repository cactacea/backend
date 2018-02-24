package io.github.cactacea.core.util.clients.facebook

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.{Http, http}
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.clients.{SocialAccount, SocialClient}

@Singleton
class FacebookHttpClient @Inject()(objectMapper: FinatraObjectMapper) extends SocialClient {

  private val hostname = "graph.facebook.com"
  private val client = Http.client.withTls(hostname).newService(s"$hostname:443")

  override def get(accessTokenKey: String, accessTokenSecret: String): Future[Option[SocialAccount]] =  {
    val request = http.Request(http.Method.Get, s"/me?access_token=$accessTokenKey")
    client(request).map({ f =>
      val g = objectMapper.parse[FacebookAccount](f.contentString)
      Some(SocialAccount(g.id))
    }).rescue({
      case _: Exception =>
        Future.None
    })
  }

}
