package io.github.cactacea.backend.specs

import com.twitter.finagle.http.Response
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.helpers.DatabaseHelper
import io.github.cactacea.backend.modules.{DefaultPushNotificationModule, DefaultQueueModule, DefaultStorageModule}
import io.github.cactacea.backend.server.DefaultServer
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.services.DatabaseProviderModule
import io.github.cactacea.core.util.tokens.AuthTokenGenerator

import scala.util.parsing.json.{JSONArray, JSONObject}

class ServerSpec extends FeatureTest {

  override val server = new EmbeddedHttpServer(
    twitterServer = new DefaultServer
  )

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    DatabaseHelper.initialize()
  }

  override protected def afterAll() = {
    super.afterAll()
    DatabaseHelper.initialize()
  }

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultPushNotificationModule,
        DefaultQueueModule,
        DefaultStorageModule,
        FinatraJacksonModule
      )
    ).create


  def signUp(accountName: String, displayName: String, password: String, udid: String) : Response = {
    server.httpPost(
      path = "/sessions",
      headers = Map(
        "X-API-KEY" -> AuthTokenGenerator.apiKey,
        "User-Agent" -> "ios"
      ),
      postBody = JSONObject(Map(
        "account_name" -> accountName,
        "display_name" -> displayName,
        "password" -> password,
        "udid" -> udid)).toString()
    )
  }

  def signIn(accountName: String, password: String, udid: String) : Response = {
    server.httpGet(
      path = s"/sessions?account_name=$accountName&password=$password&udid=$udid",
      headers = Map(
        "X-API-KEY" -> AuthTokenGenerator.apiKey,
        "User-Agent" -> "ios"
      )
    )
  }

  def signOut(accessToken: String) : Response = {
    server.httpDelete(
      path = "/session",
      headers = Map(
        "X-API-KEY" -> AuthTokenGenerator.apiKey,
        "X-AUTHORIZATION" -> accessToken
      )
    )
  }

  def postFeed(feedMessage: String, tags: List[String], feedPrivacyType: FeedPrivacyType, accessToken: String) : Response = {
    server.httpPost(
      path = "/feeds",
      headers = Map(
        "X-API-KEY" -> AuthTokenGenerator.apiKey,
        "X-AUTHORIZATION" -> accessToken
      ),
      postBody = JSONObject(Map(
        "feed_message" -> feedMessage,
        "tags" -> JSONArray(tags),
        "privacy_type" -> 0,
        "content_warning" -> 0
      )).toString()
    )
  }

}
