package io.github.cactacea.util.clients.onesignal

import java.io.File

import com.google.inject.{Provides, Singleton}
import com.twitter.finagle.Http
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule
import com.typesafe.config.ConfigFactory
import com.twitter.conversions.time._

object OneSignalHttpClientModule extends TwitterModule {

  @Singleton
  @Provides
  @OneSignalHttpClient
  def provide(mapper: FinatraObjectMapper): HttpClient = {
    val config = ConfigFactory.parseFile(new File("onesignal.conf")).atPath("onesignal")
    val hostname = config.getString("host")
    val key = "Basic " + config.getString("apiKey")
    val defaultHeaders = Map("Authorization" -> key)
    val httpService = Http.client
      .withLabel("OneSignal")
      .withRequestTimeout(config.getInt("timeout") seconds)
      .withSessionPool.minSize(config.getInt("minPoolSize"))
      .withSessionPool.maxSize(config.getInt("maxPoolSize"))
      .withSessionPool.maxWaiters(config.getInt("maxPoolWaiters"))
      .newService(hostname)
    new HttpClient(hostname = hostname, httpService = httpService, retryPolicy = None, defaultHeaders = defaultHeaders, mapper = mapper)
  }

}
