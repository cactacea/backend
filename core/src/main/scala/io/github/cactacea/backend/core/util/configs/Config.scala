package io.github.cactacea.backend.core.util.configs

import com.twitter.util.TimeConversions._
import com.typesafe.config.ConfigFactory
import io.github.cactacea.backend.core.domain.enums.DeviceType
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object Config extends DurationReader {

  private val config = ConfigFactory.load()
  private val dbConfig = config.as[DatabaseConfig]("db")
  private val authConfig = config.as[AuthConfig]("auth")
  private val passwordConfig = config.as[PasswordConfig]("password")

  object db {
    lazy val user = dbConfig.user.getOrElse("root")
    lazy val password = dbConfig.password.getOrElse("root")
    lazy val database = dbConfig.database.getOrElse("cactacea")
    lazy val dest = (dbConfig.hostname.getOrElse("localhost") + ":" + dbConfig.port.getOrElse("3306"))
    lazy val poolWatermarkLow = dbConfig.poolWatermarkLow.getOrElse(1)
    lazy val poolWatermarkMax = dbConfig.poolWatermarkMax.getOrElse(1)
  }

  object auth {

    object keys {
      lazy val ios = authConfig.iosApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
      lazy val android = authConfig.androidApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
      lazy val web = authConfig.webApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
      lazy val all = List(
        (DeviceType.ios, ios),
        (DeviceType.android, android),
        (DeviceType.web, web))
    }

    object token {
      lazy val signingKey = authConfig.signingKey.getOrElse("/hYFwm7UmiCf5tW3bL9UbxdQYw15mWSLpAsDinEacQauR6pFcBPDkGC7Kd5triYtiIR3QMlzkzRClMgcsZeS1Q\\=\\=")
      lazy val algorithm = authConfig.algorithm.getOrElse("HS256")
      lazy val expire = authConfig.expire.getOrElse(30 days)
      lazy val issuer = authConfig.issuer.getOrElse("cactacea")
      lazy val subject = authConfig.subject.getOrElse("cactacea")
    }

    object headerNames {
      lazy val apiKey = authConfig.apiKeyHeaderName.getOrElse("X-API-KEY")
      lazy val authorization = authConfig.authorizationHeaderName.getOrElse("X-AUTHORIZATION")
    }
  }

  object password {
    lazy val salt = passwordConfig.salt.getOrElse("cactacea")
    lazy val iterations = passwordConfig.iterations.getOrElse(1000)
    lazy val keyLength = passwordConfig.keyLength.getOrElse(128)
  }


  println(
    """  _____           _                         ____             _                  _
      | / ____|         | |                       |  _ \           | |                | |
      || |     __ _  ___| |_ __ _  ___ ___  __ _  | |_) | __ _  ___| | _____ _ __   __| |
      || |    / _` |/ __| __/ _` |/ __/ _ \/ _` | |  _ < / _` |/ __| |/ / _ \ '_ \ / _` |
      || |___| (_| | (__| || (_| | (_|  __/ (_| | | |_) | (_| | (__|   <  __/ | | | (_| |
      | \_____\__,_|\___|\__\__,_|\___\___|\__,_| |____/ \__,_|\___|_|\_\___|_| |_|\__,_|"""
  )

  println("---------------------------------------------------------------")
  println(s"db.user = ${db.user}")
  println(s"db.password = ${db.password}")
  println(s"db.database = ${db.database}")
  println(s"db.dest = ${db.dest}")
  println(s"db.poolWatermarkLow = ${db.poolWatermarkLow}")
  println(s"db.poolWatermarkMax = ${db.poolWatermarkMax}")
  println(s"auth.apiKeyHeaderName = ${auth.headerNames.apiKey}")
  println(s"auth.authorizationHeaderName = ${auth.headerNames.authorization}")
  println(s"auth.iosApiKey = ${auth.keys.ios}")
  println(s"auth.androidApiKey = ${auth.keys.android}")
  println(s"auth.webApiKey = ${auth.keys.web}")
  println(s"auth.signingKey = ${auth.token.signingKey}")
  println(s"auth.algorithm = ${auth.token.algorithm}")
  println(s"auth.expire = ${auth.token.expire}")
  println(s"auth.issuer = ${auth.token.issuer}")
  println(s"auth.subject = ${auth.token.subject}")
  println(s"password.salt = ${password.salt}")
  println(s"password.iterations = ${password.iterations}")
  println(s"password.keyLength = ${password.keyLength}")
  println("---------------------------------------------------------------")
  println("")

}
