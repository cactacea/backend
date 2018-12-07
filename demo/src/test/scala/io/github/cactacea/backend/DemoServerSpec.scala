package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DefaultStorageService
import io.github.cactacea.backend.helpers._
import io.github.cactacea.backend.models.responses.Authentication

@Singleton
class DemoServerSpec extends FeatureTest
  with CommonHelper
  with MediumsHelper
  with AccountsHelper
  with FollowsHelper
  with SessionHelper
  with SessionsHelper {

  override val server = new EmbeddedHttpServer(
    twitterServer = new DemoServer {
      override def storageModule: TwitterModule = DemoStorageModule
    }
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
        DemoStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        DefaultDeepLinkModule,
        DefaultJacksonModule
      )
    ).create

  val mapper = injector.instance[FinatraObjectMapper]
  val storageService = injector.instance[DefaultStorageService]

  test("create demo data") {

    cleanUp()

    createAccount("ito_hirobumi", "Password_2018")
    createAccount("kuroda_kiyotaka", "Password_2018")
    createAccount("sanjo_sanetomi", "Password_2018")
    createAccount("yamagata_aritomo", "Password_2018")
    createAccount("matsutaka_masayoshi", "Password_2018")
    createAccount("okuma_shigenobu", "Password_2018")
    createAccount("saionji_kinmochi", "Password_2018")
    createAccount("katsura_taro", "Password_2018")
    createAccount("terauchi_masatake", "Password_2018")
    createAccount("hara_takashi", "Password_2018")
    createAccount("uchida_kosai", "Password_2018")
    createAccount("takahashi_korekiyo", "Password_2018")
    createAccount("kato_tomosaburo", "Password_2018")
    createAccount("yamamoto_gonbei", "Password_2018")
    createAccount("kiyoura_keigo", "Password_2018")
    createAccount("kato_takaaki", "Password_2018")
    createAccount("wakatsuki_reijiro", "Password_2018")
    createAccount("tanaka_giichi", "Password_2018")

  }

  def createAccount(accountName: String, password: String): Authentication = {
    val authentication = signUp(accountName, password)
    val mediums = uploadMedium(accountName + ".jpg", authentication.accessToken)
    mediums.headOption.foreach(medium =>
      updateProfileImage(medium.id, authentication.accessToken)
    )
    authentication
  }

  def cleanUp(): Unit = {
    val localPath = storageService.localPath
    import java.nio.file.{Files, Paths}
    val path = Paths.get(localPath)
    if (Files.exists(path)) {
      val files = Files.list(path)
      files.forEach(f => Files.deleteIfExists(f))
      if (!Files.exists(path)) {
        Files.createDirectory(path)
      }
    } else {
      Files.createDirectory(path)
    }
  }

}
