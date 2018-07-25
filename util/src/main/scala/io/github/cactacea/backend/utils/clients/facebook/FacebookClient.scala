package io.github.cactacea.backend.utils.clients.facebook

import com.google.inject.Inject
import com.twitter.finagle.http.{Method, Request}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.util.Future
import io.github.cactacea.backend.utils.clients.FacebookHttpClient

class FacebookClient @Inject()(@FacebookHttpClient httpClient: HttpClient) {

  def me(accessToken: String): Future[Facebook] = {
    val request = Request(Method.Get, s"/me?access_token=$accessToken")
    httpClient.executeJson[Facebook](request)
  }
}
