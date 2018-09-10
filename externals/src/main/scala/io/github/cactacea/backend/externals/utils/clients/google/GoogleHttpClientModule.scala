package io.github.cactacea.backend.externals.utils.clients.google

import com.google.inject.{Provides, Singleton}
import com.twitter.conversions.time._
import com.twitter.finagle.Http
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.externals.utils.clients.GoogleHttpClient

object GoogleHttpClientModule extends TwitterModule {

  @Singleton
  @Provides
  @GoogleHttpClient
  def provide(objectMapper: FinatraObjectMapper): HttpClient = {
    val hostname = "www.googleapis.com"
    val timeout = 864000
    val minPoolSize = 10
    val maxPoolSize = 30
    val maxPoolWaiters = 100
    val httpService = Http.client
      .withTls(hostname)
      .withLabel("Google")
      .withRequestTimeout(timeout seconds)
      .withSessionPool.minSize(minPoolSize)
      .withSessionPool.maxSize(maxPoolSize)
      .withSessionPool.maxWaiters(maxPoolWaiters)
      .newService(s"$hostname:443")
    new HttpClient(hostname = hostname, httpService = httpService, mapper = objectMapper)
  }

}
