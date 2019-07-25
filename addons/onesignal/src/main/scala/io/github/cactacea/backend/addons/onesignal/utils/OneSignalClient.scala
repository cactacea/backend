package io.github.cactacea.backend.addons.onesignal.utils

import com.google.inject.Inject
import com.twitter.finagle.http.{Method, Request, Response, Version}
import com.twitter.finatra.annotations.SnakeCaseMapper
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import io.github.cactacea.addons.onesignal.OneSignalHttpClient

class OneSignalClient @Inject()(@OneSignalHttpClient httpClient: HttpClient, @SnakeCaseMapper objectMapper: FinatraObjectMapper) {

  def createNotification(n: OneSignalNotification): Future[Response] = {
    val request = Request(Version.Http11, Method.Post, "/api/v1/notifications")
    val content = objectMapper.writePrettyString(n)
    request.setContentTypeJson()
    request.setContentString(content)
    httpClient.execute(request)
  }

}
