package io.github.cactacea.backend.core.util.configs

import com.typesafe.config.{ConfigFactory, Config => TypeSafeConfig}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import io.github.cactacea.backend.core.domain.enums.DeviceType
import com.twitter.util.TimeConversions._

object Config extends DurationReader {

  private val config = ConfigFactory.load().as[TypeSafeConfig]("cactacea")
  private val dbConfig = config.as[DatabaseConfig]("db.master")
  private val authConfig = config.as[AuthConfig]("auth")
  private val passwordConfig = config.as[PasswordConfig]("password")

  object db {
    lazy val user = dbConfig.user.getOrElse("root")
    lazy val password = dbConfig.password.getOrElse("root")
    lazy val database = dbConfig.database.getOrElse("cactacea")
    lazy val dest = (dbConfig.hostname.getOrElse("localhost") + ":" + dbConfig.port.getOrElse("3306"))
    lazy val poolWatermarkLow = dbConfig.poolWatermarkLow.getOrElse(0)
    lazy val poolWatermarkMax = dbConfig.poolWatermarkMax.getOrElse(10)
  }

  object auth {
    lazy val iosApiKey = authConfig.iosApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
    lazy val androidApiKey = authConfig.androidApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
    lazy val webApiKey = authConfig.webApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")

    lazy val apiKeys = List(
      (DeviceType.ios, iosApiKey),
      (DeviceType.android, androidApiKey),
      (DeviceType.web, webApiKey))

    lazy val signingKey = authConfig.signingKey.getOrElse("/hYFwm7UmiCf5tW3bL9UbxdQYw15mWSLpAsDinEacQauR6pFcBPDkGC7Kd5triYtiIR3QMlzkzRClMgcsZeS1Q\\=\\=")
    lazy val algorithm = authConfig.algorithm.getOrElse("HS256")
    lazy val expire = authConfig.expire.getOrElse(30 days)
    lazy val issuer = authConfig.issuer.getOrElse("cactacea")
    lazy val subject = authConfig.subject.getOrElse("cactacea")
  }

  object password {
    lazy val salt = passwordConfig.salt.getOrElse("cactacea")
    lazy val iterations = passwordConfig.iterations.getOrElse(1000)
    lazy val keyLength = passwordConfig.keyLength.getOrElse(128)
  }

  println("____ ___  ____ ____ ___  ____ ____ ___  ")
  println("| __\\|  \\ | __\\|_ _\\|  \\ | __\\| __\\|  \\ ")
  println("| \\__| . \\| \\__  || | . \\| \\__|  ]_| . \\")
  println("|___/|/\\_/|___/  |/ |/\\_/|___/|___/|/\\_/")
  println("")

  println("---------------------------------------------------------------")
  println(s"db.user = ${db.user}")
  println(s"db.password = ${db.password}")
  println(s"db.database = ${db.database}")
  println(s"db.dest = ${db.dest}")
  println(s"db.poolWatermarkLow = ${db.poolWatermarkLow}")
  println(s"db.poolWatermarkMax = ${db.poolWatermarkMax}")
  println(s"auth.iosApiKey = ${auth.iosApiKey}")
  println(s"auth.androidApiKey = ${auth.androidApiKey}")
  println(s"auth.webApiKey = ${auth.webApiKey}")
  println(s"auth.signingKey = ${auth.signingKey}")
  println(s"auth.algorithm = ${auth.algorithm}")
  println(s"auth.expire = ${auth.expire}")
  println(s"auth.issuer = ${auth.issuer}")
  println(s"auth.subject = ${auth.subject}")
  println(s"password.salt = ${password.salt}")
  println(s"password.iterations = ${password.iterations}")
  println(s"password.keyLength = ${password.keyLength}")
  println("---------------------------------------------------------------")
  println("")

}
