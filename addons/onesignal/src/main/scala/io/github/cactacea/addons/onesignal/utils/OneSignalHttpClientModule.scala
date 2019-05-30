package io.github.cactacea.addons.onesignal.utils

import com.google.inject.{Provides, Singleton}
import com.twitter.finagle.Http
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule
import com.twitter.conversions.DurationOps._
import io.github.cactacea.addons.onesignal.OneSignalHttpClient

object OneSignalHttpClientModule extends TwitterModule {

  @Singleton
  @Provides
  @OneSignalHttpClient
  def provide(objectMapper: FinatraObjectMapper): HttpClient = {
    val hostname = "onesignal.com"
    val key = "Basic " + OneSignalConfig.onesignal.apiKey
    val defaultHeaders = Map("Authorization" -> key)
    val timeout = 864000
    val minPoolSize = 10
    val maxPoolSize = 30
    val maxPoolWaiters = 100
    val httpService = Http.client
      .withTls(hostname)
      .withLabel("OneSignal")
      .withRequestTimeout(timeout seconds)
      .withSessionPool.minSize(minPoolSize)
      .withSessionPool.maxSize(maxPoolSize)
      .withSessionPool.maxWaiters(maxPoolWaiters)
      .newService(s"${hostname}:443")
    new HttpClient(hostname = hostname, httpService = httpService, retryPolicy = None, defaultHeaders = defaultHeaders, mapper = objectMapper)
  }

}
