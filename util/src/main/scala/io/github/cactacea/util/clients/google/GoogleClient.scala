package io.github.cactacea.util.clients.google

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Method, Request}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.util.Future
import com.typesafe.config.ConfigFactory
import io.github.cactacea.util.clients.GoogleHttpClient

@Singleton
class GoogleClient @Inject()(@GoogleHttpClient httpClient: HttpClient) {

  def me(accessToken: String): Future[Google] = {
    val config = ConfigFactory.load("google.conf").getConfig("google")
    val apiKey = config.getString("apiKey")
    val request = Request(Method.Get, s"/plus/v1/people/me?api_key=$apiKey&access_token=$accessToken")
    httpClient.executeJson[Google](request)
  }

}
