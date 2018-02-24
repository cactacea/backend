package io.github.cactacea.core.infrastructure.clients.onesignal

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request, Response, Version}
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import com.typesafe.config.ConfigFactory

@Singleton
class OneSignalHttpClient {

  @Inject var objectMapper: FinatraObjectMapper = _

  private val config = ConfigFactory.load()
  private val authenticationKey = config.getString("onesignal.authenticationKey")
  private val client = Http.client
    .withLabel("OneSignal")
    .newService("https://onesignal.com/api/v1/")

  def post(path: String, content: Any): Future[Response] = {
    val request = Request(Version.Http11, Method.Post, path)
    request.setContentTypeJson()
    request.setContentString(objectMapper.writePrettyString(content))
    request.headerMap.add("Authorization", "Basic " + authenticationKey)
    client(request)
  }

}
