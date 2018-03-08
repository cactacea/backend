package io.github.cactacea.backend.onesignal

import com.google.inject.Inject
import com.twitter.finagle.http.{Method, Request, Response, Version}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import com.typesafe.config.ConfigFactory

class OneSignalClient @Inject()(@OneSignalHttpClient httpClient: HttpClient, objectMapper: FinatraObjectMapper) {

  val config = ConfigFactory.load("onesignal.conf").getConfig("onesignal")
  val appId = config.getString("appId")

  def createNotification(n: OneSignalNotification): Future[Response] = {
    val request = Request(Version.Http11, Method.Post, "/api/v1/notifications")
    request.setContentTypeJson()
    request.setContentString(objectMapper.writePrettyString(n))
    httpClient.execute(request)
  }

}
