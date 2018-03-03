package io.github.cactacea.util.clients.onesignal

import java.io.File

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Method, Request, Response, Version}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import com.typesafe.config.ConfigFactory

@Singleton
class OneSignalClient @Inject()(@OneSignalHttpClient httpClient: HttpClient, objectMapper: FinatraObjectMapper) {

  val appId = ConfigFactory.parseFile(new File("onesignal.conf")).atPath("onesignal").getString("appId")

  def createNotification(n: OneSignalNotification): Future[Response] = {
    val request = Request(Version.Http11, Method.Post, "/notifications")
    request.setContentTypeJson()
    request.setContentString(objectMapper.writePrettyString(n))
    httpClient.execute(request)
  }

}
