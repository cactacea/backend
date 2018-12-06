package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.helpers._

@Singleton
class DemoServerSpec extends FeatureTest
  with CommonHelper
  with AccountsHelper
  with FollowsHelper
  with SessionHelper
  with SessionsHelper {

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

  val mapper = injector.instance[FinatraObjectMapper]


  test("create demo data") {

    signUp("ito_hirobumi1", "Password_2018")
    signUp("kuroda_kiyotaka", "Password_2018")
    signUp("sanjo_sanetomi", "Password_2018")
    signUp("yamagata_aritomo", "Password_2018")
    signUp("matsutaka_masayoshi", "Password_2018")
    signUp("okuma_shigenobu", "Password_2018")
    signUp("saionji_kinmochi", "Password_2018")
    signUp("katsura_taro", "Password_2018")
    signUp("terauchi_masatake", "Password_2018")
    signUp("hara_takashi", "Password_2018")
    signUp("uchida_kosai", "Password_2018")
    signUp("takahashi_korekiyo", "Password_2018")
    signUp("kato_tomosaburo", "Password_2018")
    signUp("yamamoto_gonbei", "Password_2018")
    signUp("kiyoura_keigo", "Password_2018")
    signUp("kato_takaaki", "Password_2018")
    signUp("wakatsuki_reijiro", "Password_2018")
    signUp("tanaka_giichi", "Password_2018")

  }

}
