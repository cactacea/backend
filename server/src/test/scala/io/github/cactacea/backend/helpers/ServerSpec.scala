package io.github.cactacea.backend.helpers

import com.twitter.finagle.http.Response
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.CactaceaServer
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.util.configs.Config

class ServerSpec extends FeatureTest {

  override val server = new EmbeddedHttpServer(
    twitterServer = new CactaceaServer
  )

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultInjectionModule,
        DefaultNotificationModule,
        DefaultNotificationMessagesModule,
        DefaultPublishModule,
        DefaultPushNotificationModule,
        DefaultStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        DefaultDeepLinkModule,
        DefaultJacksonModule
      )
    ).create


  val root = ""
  def post(path: String, postBody: String, accessToken: String): Response = {
    server.httpPost(
      path = root + path,
      headers = Map(
        Config.auth.headerNames.apiKey -> Config.auth.keys.all.head._2,
        "X-AUTHORIZATION" -> accessToken
      ),
      postBody = postBody
    )
  }

  def delete(path: String, accessToken: String): Response = server.httpDelete(
    path = root + path,
    headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.all.head._2,
      "X-AUTHORIZATION" -> accessToken
    )
  )

  def post(path: String, postBody: String) : Response = {
    server.httpPost(
      path = root + path,
      headers = Map(
        Config.auth.headerNames.apiKey -> Config.auth.keys.all.head._2,
        "User-Agent" -> "ios"
      ),
      postBody = postBody
    )
  }

  def get(path: String) : Response = {
    server.httpGet(
      path = root + path,
      headers = Map(
        Config.auth.headerNames.apiKey -> Config.auth.keys.all.head._2,
        "User-Agent" -> "ios"
      )
    )
  }

  def get(path: String, accessToken: String): Response = {
    server.httpGet(
      path = root + path,
      headers = Map(
        Config.auth.headerNames.apiKey -> Config.auth.keys.all.head._2,
        "X-AUTHORIZATION" -> accessToken
      )
    )
  }

}
