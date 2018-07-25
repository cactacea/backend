package io.github.cactacea.backend.utils.clients.facebook

import com.google.inject.{Provides, Singleton}
import com.twitter.conversions.time._
import com.twitter.finagle.Http
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.utils.clients.FacebookHttpClient

object FacebookHttpClientModule extends TwitterModule {

  @Provides
  @Singleton
  @FacebookHttpClient
  def provide(objectMapper: FinatraObjectMapper): HttpClient = {
    val hostname = "graph.facebook.com"
    val timeout = 864000
    val minPoolSize = 10
    val maxPoolSize = 30
    val maxPoolWaiters = 100
    val httpService = Http.client
      .withTls(hostname)
      .withLabel("Facebook")
      .withRequestTimeout(timeout seconds)
      .withSessionPool.minSize(minPoolSize)
      .withSessionPool.maxSize(maxPoolSize)
      .withSessionPool.maxWaiters(maxPoolWaiters)
      .newService(s"$hostname:443")
    new HttpClient(hostname = hostname, httpService = httpService, mapper = objectMapper)
  }
}
