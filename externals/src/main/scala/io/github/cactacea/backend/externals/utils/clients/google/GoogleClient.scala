package io.github.cactacea.backend.externals.utils.clients.google

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Method, Request}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.util.Future
import io.github.cactacea.backend.externals.configs.Config
import io.github.cactacea.backend.externals.utils.clients.GoogleHttpClient

@Singleton
class GoogleClient @Inject()(@GoogleHttpClient httpClient: HttpClient) {

  def me(accessToken: String): Future[Google] = {
    val apiKey = Config.google.apiKey
    val request = Request(Method.Get, s"/plus/v1/people/me?api_key=$apiKey&access_token=$accessToken")
    httpClient.executeJson[Google](request)
  }

}
