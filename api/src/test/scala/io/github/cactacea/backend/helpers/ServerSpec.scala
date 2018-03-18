package io.github.cactacea.backend.helpers

import com.google.inject.Inject
import com.twitter.finagle.http.Response
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.BackendServer
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.components.modules.{DefaultConfigModule, _}

class ServerSpec extends FeatureTest {

  @Inject private var configService: ConfigService = _
  @Inject private var objectMapper: FinatraObjectMapper = _

  override val server = new EmbeddedHttpServer(
    twitterServer = new BackendServer
  )

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        FGTSocialAccountsModule,
        NoActionInjectionModule,
        DefaultConfigModule,
        DefaultNotificationModule,
        DefaultNotificationMessagesModule,
        NoQueuePublishModule,
        NoPushNotificationModule,
        LocalStorageModule,
        NoQueueSubScribeModule,
        ImageTranscodeModule,
        InstagramDesignIdentifyModule,
        DefaultDeepLinkModule,
        FinatraJacksonModule
      )
    ).create




  def signOut(accessToken: String) : Response = {
    delete("/session", accessToken)
  }


  def post(path: String, postBody: String, accessToken: String): Response = {
    server.httpPost(
      path = path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "X-AUTHORIZATION" -> accessToken
      ),
      postBody = postBody
    )
  }

  def delete(path: String, accessToken: String): Response = {
    server.httpDelete(
      path = path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "X-AUTHORIZATION" -> accessToken
      )
    )
  }

  def post(path: String, postBody: String) : Response = {
    server.httpPost(
      path = "/sessions",
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "User-Agent" -> "ios"
      ),
      postBody = postBody
    )
  }

  def get(path: String) : Response = {
    server.httpGet(
      path = path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "User-Agent" -> "ios"
      )
    )
  }

  def get(path: String, accessToken: String): Response = {
    server.httpGet(
      path = path,
      headers = Map(
        "X-API-KEY" -> configService.apiKeys.head._2,
        "X-AUTHORIZATION" -> accessToken
      )
    )
  }

}
