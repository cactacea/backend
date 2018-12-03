package io.github.cactacea.backend

import java.nio.file.{Files, Paths}
import java.util.UUID

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{FileElement, RequestBuilder}
import com.twitter.finatra.http.routing.HttpWarmup
import com.twitter.finatra.httpclient.RequestBuilder._
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.utils.Handler
import com.twitter.io.Buf
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.models.requests.session.PutSessionProfileImage
import io.github.cactacea.backend.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.models.responses.{Authentication, MediumCreated}

@Singleton
class DemoSetupHandler  @Inject()(mapper: FinatraObjectMapper, warmup: HttpWarmup) extends Handler {

  override def handle(): Unit = {
    DemoSetup.execute()

    try {
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

    } catch {
      case e: Throwable =>
        println(e)
    }

  }

  def createAccount(accountName: String, password: String): Unit = {
    val agent = Some("Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1")
    val uuid = UUID.randomUUID().toString
    val postSignUp = PostSignUp(accountName, password, uuid, agent)
    val body = mapper.writePrettyString(postSignUp)
    val request = post("/sessions").body(body).header(Config.auth.headerNames.apiKey, Config.auth.keys.all.head._2)

    warmup.send(request, false, 1, { response =>
      val auth = mapper.parse[Authentication](response.contentString)
      val bytes = Files.readAllBytes(Paths.get(this.getClass.getClassLoader.getResource(s"$accountName.jpg").toURI))

      // TODO
      val request = RequestBuilder().url("http://localhost:9000/mediums")
        .add(FileElement("file", Buf.ByteArray.Owned(bytes), Some("image/jpeg"), Some("marathon.jpg")))
        .addHeader(Config.auth.headerNames.apiKey, Config.auth.keys.all.head._2)
        .addHeader(Config.auth.headerNames.authorizationKey, auth.accessToken)
        .buildFormPost(multipart = true)

      warmup.send(request, false, 1, { response =>
        val medium = mapper.parse[MediumCreated](response.contentString)
        val putSessionProfileImage = PutSessionProfileImage(medium.id)
        val body = mapper.writePrettyString(putSessionProfileImage)
        val request = put("/session/profile_image").body(body)
          .header(Config.auth.headerNames.apiKey, Config.auth.keys.all.head._2)
          .header(Config.auth.headerNames.authorizationKey, auth.accessToken)
        warmup.send(request, false, 1, { response =>
          println(response.contentString)
        })
      })
    })

  }



}
