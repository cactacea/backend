package io.github.cactacea.backend

import java.io.PrintWriter

import com.google.inject.Singleton
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.helpers._

@Singleton
class DemoServerSpec extends FeatureTest with Helpers {

  override val server = new EmbeddedHttpServer(
    twitterServer = new CactaceaServer {
      override def storageModule: TwitterModule = DemoStorageModule
      override def warmup() {
      }
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
  val storageService = injector.instance[DemoStorageService]

  test("create swagger.json") {

    val swagger = server.httpGet(s"/docs/model")
    assert(swagger.contentString.isEmpty == false)

    val path = CactaceaBuildInfo.baseDirectory.getParent + "/docs/swagger.json"
    val file = new PrintWriter(path)
    file.write(swagger.contentString)
    file.close()

  }

  test("create demo data") {

    cleanUp()

    val aritomo_yamagata = createAccount("aritomo_yamagata","Password_2018")
    val eisaku_sato = createAccount("eisaku_sato","Password_2018")
    val giichi_tanaka = createAccount("giichi_tanaka","Password_2018")
    val gonbei_yamamoto = createAccount("gonbei_yamamoto","Password_2018")
    val hayato_ikeda = createAccount("hayato_ikeda","Password_2018")
    val hideki_tojo = createAccount("hideki_tojo","Password_2018")
    val hirobumi_ito = createAccount("hirobumi_ito","Password_2018")
    val hitoshi_ashida = createAccount("hitoshi_ashida","Password_2018")
    val ichiro_hatoyama = createAccount("ichiro_hatoyama","Password_2018")
    val junichiro_koizumi = createAccount("junichiro_koizumi","Password_2018")
    val kakuei_tanaka = createAccount("kakuei_tanaka","Password_2018")
    val kantaro_suzuki = createAccount("kantaro_suzuki","Password_2018")
    val keigo_kiyoura = createAccount("keigo_kiyoura","Password_2018")
    val keisuke_okada = createAccount("keisuke_okada","Password_2018")
    val keizo_obuchi = createAccount("keizo_obuchi","Password_2018")
    val kiichi_miyazawa = createAccount("kiichi_miyazawa","Password_2018")
    val kiichiro_hiranumra = createAccount("kiichiro_hiranumra","Password_2018")
    val kijuro_shidehara = createAccount("kijuro_shidehara","Password_2018")
    val kinmochi_saionji = createAccount("kinmochi_saionji","Password_2018")
    val kiyotaka_kuroda = createAccount("kiyotaka_kuroda","Password_2018")
    val kohki_hirota = createAccount("kohki_hirota","Password_2018")
    val korekiyo_takahashi = createAccount("korekiyo_takahashi","Password_2018")
    val kosai_uchida = createAccount("kosai_uchida","Password_2018")
    val kuniaki_koiso = createAccount("kuniaki_koiso","Password_2018")
    val makoto_saito = createAccount("makoto_saito","Password_2018")
    val masayoshi_ito = createAccount("masayoshi_ito","Password_2018")
    val masayoshi_matsutaka = createAccount("masayoshi_matsutaka","Password_2018")
    val masayoshi_ohira = createAccount("masayoshi_ohira","Password_2018")
    val mitsumasa_yonai = createAccount("mitsumasa_yonai","Password_2018")
    val morihiro_hosokawa = createAccount("morihiro_hosokawa","Password_2018")
    val naoto_kan = createAccount("naoto_kan","Password_2018")
    val naruhiko_higashikuni = createAccount("naruhiko_higashikuni","Password_2018")
    val noboru_takeshita = createAccount("noboru_takeshita","Password_2018")
    val nobusuke_kishi = createAccount("nobusuke_kishi","Password_2018")
    val nobuyuki_abe = createAccount("nobuyuki_abe","Password_2018")
    val okuma_shigenobu = createAccount("okuma_shigenobu","Password_2018")
    val osachi_hamaguchi = createAccount("osachi_hamaguchi","Password_2018")
    val reijiro_wakatsuki = createAccount("reijiro_wakatsuki","Password_2018")
    val ryutaro_hashimoto = createAccount("ryutaro_hashimoto","Password_2018")
    val sanetomi_sanjo = createAccount("sanetomi_sanjo","Password_2018")
    val senjuro_hayashi = createAccount("senjuro_hayashi","Password_2018")
    val shigeru_yoshida = createAccount("shigeru_yoshida","Password_2018")
    val shinzo_abe = createAccount("shinzo_abe","Password_2018")
    val sosuke_uno = createAccount("sosuke_uno","Password_2018")
    val takaaki_kato = createAccount("takaaki_kato","Password_2018")
    val takashi_hara = createAccount("takashi_hara","Password_2018")
    val takeo_fukuda = createAccount("takeo_fukuda","Password_2018")
    val takeo_miki = createAccount("takeo_miki","Password_2018")
    val tanzan_ishibashi = createAccount("tanzan_ishibashi","Password_2018")
    val taro_aso = createAccount("taro_aso","Password_2018")
    val taro_katsura = createAccount("taro_katsura","Password_2018")
    val terauchi_masatake = createAccount("terauchi_masatake","Password_2018")
    val tetsu_katayama = createAccount("tetsu_katayama","Password_2018")
    val tomiichi_murayama = createAccount("tomiichi_murayama","Password_2018")
    val tomosaburo_kato = createAccount("tomosaburo_kato","Password_2018")
    val toshiki_kaifu = createAccount("toshiki_kaifu","Password_2018")
    val tsutomu_hata = createAccount("tsutomu_hata","Password_2018")
    val tsuyoshi_inukai = createAccount("tsuyoshi_inukai","Password_2018")
    val yasuhiro_nakasone = createAccount("yasuhiro_nakasone","Password_2018")
    val yasuo_fukuda = createAccount("yasuo_fukuda","Password_2018")
    val yoshihiko_noda = createAccount("yoshihiko_noda","Password_2018")
    val yoshiro_mori = createAccount("yoshiro_mori","Password_2018")
    val yukio_hatoyama = createAccount("yukio_hatoyama","Password_2018")
    val zenko_suzuki = createAccount("zenko_suzuki","Password_2018")

    createFollow(shinzo_abe, aritomo_yamagata)
    createFollow(shinzo_abe, eisaku_sato)
    createFollow(shinzo_abe, giichi_tanaka)
    createFollow(shinzo_abe, gonbei_yamamoto)
    createFollow(shinzo_abe, hayato_ikeda)
    createFollow(shinzo_abe, hideki_tojo)
    createFollow(shinzo_abe, hirobumi_ito)
    createFollow(shinzo_abe, hitoshi_ashida)
    createFollow(shinzo_abe, ichiro_hatoyama)
    createFollow(shinzo_abe, junichiro_koizumi)
    createFollow(shinzo_abe, kakuei_tanaka)
    createFollow(shinzo_abe, kantaro_suzuki)
    createFollow(shinzo_abe, keigo_kiyoura)
    createFollow(shinzo_abe, keisuke_okada)
    createFollow(shinzo_abe, keizo_obuchi)
    createFollow(shinzo_abe, kiichi_miyazawa)
    createFollow(shinzo_abe, kiichiro_hiranumra)
    createFollow(shinzo_abe, kijuro_shidehara)
    createFollow(shinzo_abe, kinmochi_saionji)
    createFollow(shinzo_abe, kiyotaka_kuroda)
    createFollow(shinzo_abe, kohki_hirota)
    createFollow(shinzo_abe, korekiyo_takahashi)
    createFollow(shinzo_abe, kosai_uchida)
    createFollow(shinzo_abe, kuniaki_koiso)
    createFollow(shinzo_abe, makoto_saito)
    createFollow(shinzo_abe, masayoshi_ito)
    createFollow(shinzo_abe, masayoshi_matsutaka)
    createFollow(shinzo_abe, masayoshi_ohira)
    createFollow(shinzo_abe, mitsumasa_yonai)
    createFollow(shinzo_abe, morihiro_hosokawa)
    createFollow(shinzo_abe, naoto_kan)
    createFollow(shinzo_abe, naruhiko_higashikuni)
    createFollow(shinzo_abe, noboru_takeshita)
    createFollow(shinzo_abe, nobusuke_kishi)
    createFollow(shinzo_abe, nobuyuki_abe)
    createFollow(shinzo_abe, okuma_shigenobu)
    createFollow(shinzo_abe, osachi_hamaguchi)
    createFollow(shinzo_abe, reijiro_wakatsuki)
    createFollow(shinzo_abe, ryutaro_hashimoto)
    createFollow(shinzo_abe, sanetomi_sanjo)
    createFollow(shinzo_abe, senjuro_hayashi)
    createFollow(shinzo_abe, shigeru_yoshida)
    createFollow(shinzo_abe, sosuke_uno)
    createFollow(shinzo_abe, takaaki_kato)
    createFollow(shinzo_abe, takashi_hara)
    createFollow(shinzo_abe, takeo_fukuda)
    createFollow(shinzo_abe, takeo_miki)
    createFollow(shinzo_abe, tanzan_ishibashi)
    createFollow(shinzo_abe, taro_aso)
    createFollow(shinzo_abe, taro_katsura)
    createFollow(shinzo_abe, terauchi_masatake)
    createFollow(shinzo_abe, tetsu_katayama)
    createFollow(shinzo_abe, tomiichi_murayama)
    createFollow(shinzo_abe, tomosaburo_kato)
    createFollow(shinzo_abe, toshiki_kaifu)
    createFollow(shinzo_abe, tsutomu_hata)
    createFollow(shinzo_abe, tsuyoshi_inukai)
    createFollow(shinzo_abe, yasuhiro_nakasone)
    createFollow(shinzo_abe, yasuo_fukuda)
    createFollow(shinzo_abe, yoshihiko_noda)
    createFollow(shinzo_abe, yoshiro_mori)
    createFollow(shinzo_abe, yukio_hatoyama)
    createFollow(shinzo_abe, zenko_suzuki)

    createFollow(aritomo_yamagata, yasuhiro_nakasone)
    createFollow(aritomo_yamagata, yasuo_fukuda)
    createFollow(aritomo_yamagata, yoshihiko_noda)
    createFollow(aritomo_yamagata, yoshiro_mori)
    createFollow(aritomo_yamagata, yukio_hatoyama)
    createFollow(aritomo_yamagata, zenko_suzuki)
    createFollow(aritomo_yamagata, shinzo_abe)

    createFeed("Uesugi Kenshin (上杉 謙信, February 18, 1530 – April 19, 1578[1]) was a daimyō who was" +
      " born as Nagao Kagetora,[2] and after the adoption into the Uesugi clan, ruled Echigo Province in the" +
      " Sengoku period of Japan.[3] He was one of the most powerful daimyōs of the Sengoku period. While chiefly" +
      " remembered for his prowess on the battlefield, Kenshin is also regarded as an extremely skillful" +
      " administrator who fostered the growth of local industries and trade; his rule saw a marked rise in" +
      " the standard of living of Echigo.",
      Some("hideyosi_toyotomi"),
      Some(Array("daimyo", "sengoku")),
      FeedPrivacyType.everyone,
      false, shinzo_abe)

    createFeed("Oda Nobunaga (help·info), June 23, 1534 – June 21, 1582) was a powerful daimyō (feudal lord) of " +
      "Japan in the late 16th century who attempted to unify Japan during the late Sengoku period, and successfully" +
      " gained control over most of Honshu. Nobunaga is regarded as one of three unifiers of Japan along with his" +
      " retainers Toyotomi Hideyoshi and Tokugawa Ieyasu. During his later life, Nobunaga was widely known for most" +
      " brutal suppression of determined opponents, eliminating those who by principle refused to cooperate or yield" +
      " to his demands. His reign was noted for innovative military tactics, fostering free trade, and encouraging" +
      " the start of the Momoyama historical art period. He was killed when his retainer Akechi Mitsuhide rebelled" +
      " against him at Honnō-ji.",
      Some("nobunaga_oda"),
      Some(Array("daimyo", "sengoku")),
      FeedPrivacyType.everyone,
      false, aritomo_yamagata)

    createFeed("Tokugawa Ieyasu (徳川家康, January 30, 1543 – June 1, 1616) was the founder and first shōgun" +
      " of the Tokugawa shogunate of Japan, which effectively ruled Japan from the Battle of Sekigahara in 1600" +
      " until the Meiji Restoration in 1868. Ieyasu seized power in 1600, received appointment as shōgun in 1603," +
      " and abdicated from office in 1605, but remained in power until his death in 1616. His given name is" +
      " sometimes spelled Iyeyasu,[1][2] according to the historical pronunciation of the kana character he." +
      " Ieyasu was posthumously enshrined at Nikkō Tōshō-gū with the name Tōshō Daigongen (東照大権現). " +
      "He was one of the three unifiers of Japan, along with his former lord Nobunaga and Toyotomi Hideyoshi.",
      Some("ieyasu_tokugawa"),
      Some(Array("daimyo", "sengoku")),
      FeedPrivacyType.everyone,
      false, aritomo_yamagata)

    createFeed("Toyotomi Hideyoshi (豊臣 秀吉, March 17, 1537 – September 18, 1598) was a preeminent" +
      " daimyō, warrior, general, samurai, and politician of the Sengoku period[1] who is regarded as Japan's" +
      " second \"great unifier\".[2] He succeeded his former liege lord, Oda Nobunaga, and brought an end to the" +
      " Warring Lords period. The period of his rule is often called the Momoyama period, named after Hideyoshi's" +
      " castle. After his death, his young son Hideyori was displaced by Tokugawa Ieyasu.",
      Some("hideyosi_toyotomi"),
      Some(Array("daimyo", "sengoku")),
      FeedPrivacyType.everyone,
      false, aritomo_yamagata)

    createFeed("Yasuke (variously rendered as 弥助 or 弥介, 彌助 or 彌介 in different sources.[1])" +
      " (b. c. 1555–1590) was a Samurai of African origin who served under the Japanese hegemon and warlord" +
      " Oda Nobunaga in 1581 and 1582.",
      Some("yasuke"),
      Some(Array("samurai", "sengoku")),
      FeedPrivacyType.friends,
      false, aritomo_yamagata)


  }


}
