package io.github.cactacea.backend.helpers

import com.google.inject.Inject
import com.twitter.finagle.http.Response
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.BackendServer
import io.github.cactacea.backend.core.application.components.interfaces.ConfigService
import io.github.cactacea.backend.core.application.components.modules.{DefaultConfigModule, _}

class ServerSpec extends FeatureTest {

  @Inject private var configService: ConfigService = _

  override val server = new EmbeddedHttpServer(
    twitterServer = new BackendServer
  )

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultSocialAccountsModule,
        DefaultInjectionModule,
        DefaultConfigModule,
        DefaultNotificationModule,
        DefaultNotificationMessagesModule,
        DefaultPublishModule,
        DefaultPushNotificationModule,
        DefaultStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        DefaultIdentifyModule,
        DefaultDeepLinkModule,
        FinatraJacksonModule
      )
    ).create


  val root = ""
  def post(path: String, postBody: String, accessToken: String): Response = {
    server.httpPost(
      path = root + path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "X-AUTHORIZATION" -> accessToken
      ),
      postBody = postBody
    )
  }

  def delete(path: String, accessToken: String): Response = {
    server.httpDelete(
      path = root + path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "X-AUTHORIZATION" -> accessToken
      )
    )
  }

  def post(path: String, postBody: String) : Response = {
    server.httpPost(
      path = root + path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "User-Agent" -> "ios"
      ),
      postBody = postBody
    )
  }

  def get(path: String) : Response = {
    server.httpGet(
      path = root + path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "User-Agent" -> "ios"
      )
    )
  }

  def get(path: String, accessToken: String): Response = {
    server.httpGet(
      path = root + path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "X-AUTHORIZATION" -> accessToken
      )
    )
  }

}