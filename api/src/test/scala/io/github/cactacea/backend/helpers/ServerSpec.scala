package io.github.cactacea.backend.helpers

import com.google.inject.Inject
import com.twitter.finagle.http.Response
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.DefaultServer
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.components.modules.{DefaultConfigModule, _}
import io.github.cactacea.core.domain.enums.FeedPrivacyType

import scala.util.parsing.json.{JSONArray, JSONObject}

class ServerSpec extends FeatureTest {

  @Inject private var configService: ConfigService = _

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
        DefaultSocialAccountsModule,
        DefaultInjectionModule,
        DefaultConfigModule,
        DefaultFanOutModule,
        DefaultNotificationMessagesModule,
        DefaultPublishModule,
        DefaultPushNotificationModule,
        DefaultStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        DefaultIdentifyModule,
        FinatraJacksonModule
      )
    ).create


  def signUp(accountName: String, displayName: String, password: String, udid: String) : Response = {
    server.httpPost(
      path = "/sessions",
      headers = Map(
        "X-API-KEY" -> configService.apiKey,
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
        "X-API-KEY" -> configService.apiKey,
        "User-Agent" -> "ios"
      )
    )
  }

  def signOut(accessToken: String) : Response = {
    server.httpDelete(
      path = "/session",
      headers = Map(
        "X-API-KEY" -> configService.apiKey,
        "X-AUTHORIZATION" -> accessToken
      )
    )
  }

  def postFeed(feedMessage: String, tags: List[String], feedPrivacyType: FeedPrivacyType, accessToken: String) : Response = {
    server.httpPost(
      path = "/feeds",
      headers = Map(
        "X-API-KEY" -> configService.apiKey,
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
