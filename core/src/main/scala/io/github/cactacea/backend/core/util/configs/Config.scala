package io.github.cactacea.backend.core.util.configs

import java.net.InetAddress

import com.twitter.conversions.DurationOps._
import com.twitter.conversions.StorageUnitOps._
import com.twitter.util.Duration
import com.typesafe.config.ConfigFactory
import io.github.cactacea.backend.core.domain.enums.DeviceType
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object Config extends DurationReader {

  private val config = ConfigFactory.load()
  private val crypterConfig = config.as[CrypterConfig]("crypter")
  private val masterDBConfig = config.as[DatabaseConfig]("db.master")
  private val slaveDBConfig = config.as[DatabaseConfig]("db.slave")
  private val authConfig = config.as[AuthConfig]("auth")
  private val passwordConfig = config.as[PasswordConfig]("password")
  private val storageConfig = config.as[StorageConfig]("storage")
  private val facebookConfig = config.as[OAuth2Config]("socials.facebook")
  private val googleConfig = config.as[OAuth2Config]("socials.google")
  private val twitterConfig = config.as[OAuth1Config]("socials.twitte")
  object db { // scalastyle:ignore

    trait config {
      val user: String
      val password: String
      val database: String
      val dest: String
      val lowWatermark: Int
      val highWatermark: Int
      val idleTime: Duration
      val bufferSize: Int
      val maxPrepareStatements: Int
      val connectTimeout: Int
      val maxWaiters: Int
    }

    object master extends config { // scalastyle:ignore
      lazy val user = masterDBConfig.user.getOrElse("cactacea")
      lazy val password = masterDBConfig.password.getOrElse("cactacea")
      lazy val database = masterDBConfig.database.getOrElse("cactacea")
      lazy val dest = masterDBConfig.dest.getOrElse("localhost:3306")
      lazy val options = masterDBConfig.options.getOrElse("")
      lazy val lowWatermark = masterDBConfig.lowWatermark.getOrElse(0)
      lazy val highWatermark = masterDBConfig.highWatermark.getOrElse(50)
      lazy val idleTime = masterDBConfig.idleTime.getOrElse(Duration.Top)
      lazy val bufferSize = masterDBConfig.bufferSize.getOrElse(0)
      lazy val maxPrepareStatements = masterDBConfig.maxPrepareStatements.getOrElse(20)
      lazy val connectTimeout = masterDBConfig.connectTimeout.getOrElse(1)
      lazy val maxWaiters = masterDBConfig.maxWaiters.getOrElse(Int.MaxValue)
    }

    lazy val useSlave = slaveDBConfig.dest.isDefined

    object slave extends config { // scalastyle:ignore
      lazy val user = slaveDBConfig.user.getOrElse("cactacea")
      lazy val password = slaveDBConfig.password.getOrElse("cactacea")
      lazy val database = slaveDBConfig.database.getOrElse("cactacea")
      lazy val dest = slaveDBConfig.dest.getOrElse("localhost:3306")
      lazy val options = slaveDBConfig.options.getOrElse("")
      lazy val lowWatermark = slaveDBConfig.lowWatermark.getOrElse(0)
      lazy val highWatermark = slaveDBConfig.highWatermark.getOrElse(50)
      lazy val idleTime = slaveDBConfig.idleTime.getOrElse(Duration.Top)
      lazy val bufferSize = slaveDBConfig.bufferSize.getOrElse(0)
      lazy val maxPrepareStatements = slaveDBConfig.maxPrepareStatements.getOrElse(20)
      lazy val connectTimeout = slaveDBConfig.connectTimeout.getOrElse(1)
      lazy val maxWaiters = slaveDBConfig.maxWaiters.getOrElse(Int.MaxValue)
    }


  }

  object storage { // scalastyle:ignore
    lazy val hostName = storageConfig.hostName.getOrElse(InetAddress.getLocalHost().getHostName())
    lazy val port = storageConfig.port.getOrElse(":9000")
    lazy val maxFileSize = storageConfig.maxFileSize.getOrElse(1.megabytes)
  }

  object auth { // scalastyle:ignore

    object keys { // scalastyle:ignore
      lazy val ios = authConfig.iosApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
      lazy val android = authConfig.androidApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
      lazy val web = authConfig.webApiKey.getOrElse("78290547-ddd6-4cf2-8fe4-7dd241da3061")
      lazy val all = Seq(
        (DeviceType.ios, ios),
        (DeviceType.android, android),
        (DeviceType.web, web))
    }

    object token { // scalastyle:ignore
      lazy val signingKey = authConfig.signingKey.getOrElse("/hYFwm7UmiCf5tW3bL9UbxdQYw15mWSLpAsDinEacQauR6pFcBPDkGC7Kd5triYtiIR3QMlzkzRClMgcsZeS1Q\\=\\=")
      lazy val algorithm = authConfig.algorithm.getOrElse("HS256")
      lazy val expire = authConfig.expire.getOrElse(30 days)
      lazy val issuer = authConfig.issuer.getOrElse("cactacea")
      lazy val subject = authConfig.subject.getOrElse("cactacea")
    }

    object headerNames { // scalastyle:ignore
      lazy val apiKey = authConfig.apiKeyHeaderName.getOrElse("x-api-key")
      lazy val authorizationKey = authConfig.authorizationHeaderName.getOrElse("Authorization")
    }

  }

  object password { // scalastyle:ignore
    lazy val salt = passwordConfig.salt.getOrElse("cactacea")
    lazy val iterations = passwordConfig.iterations.getOrElse(1000)
    lazy val keyLength = passwordConfig.keyLength.getOrElse(128)
  }

  object crypter { // scalastyle:ignore
    lazy val signerKey = crypterConfig.signerKey.getOrElse("todo")
    lazy val crypterKey = crypterConfig.crypterKey.getOrElse("todo")
  }

  object facebook { // scalastyle:ignore
    val authorizationURL = facebookConfig.authorizationURL
    val accessTokenURL = facebookConfig.accessTokenURL.getOrElse("")
    val redirectURL = facebookConfig.redirectURL
    val clientID = facebookConfig.clientID.getOrElse("")
    val clientSecret = facebookConfig.clientSecret.getOrElse("")
    val scope = facebookConfig.scope
  }

  object google { // scalastyle:ignore
    val authorizationURL = googleConfig.authorizationURL
    val accessTokenURL = googleConfig.accessTokenURL.getOrElse("")
    val redirectURL = googleConfig.redirectURL
    val clientID = googleConfig.clientID.getOrElse("")
    val clientSecret = googleConfig.clientSecret.getOrElse("")
    val scope = googleConfig.scope
  }

  object twitter { // scalastyle:ignore
    val requestTokenURL = twitterConfig.requestTokenURL.getOrElse("")
    val accessTokenURL = twitterConfig.accessTokenURL.getOrElse("")
    val authorizationURL = twitterConfig.authorizationURL.getOrElse("")
    val callbackURL = twitterConfig.callbackURL.getOrElse("")
    val consumerKey = twitterConfig.consumerKey.getOrElse("")
    val consumerSecret = twitterConfig.consumerSecret.getOrElse("")
  }

  println( // scalastyle:ignore
"""   ___           _                           ___            _                  _
    / __\__ _  ___| |_ __ _  ___ ___  __ _    / __\ __ _  ___| | _____ _ __   __| |
   / /  / _` |/ __| __/ _` |/ __/ _ \/ _` |  /__\/// _` |/ __| |/ / _ \ '_ \ / _` |
  / /__| (_| | (__| || (_| | (_|  __/ (_| | / \/  \ (_| | (__|   <  __/ | | | (_| |
  \____/\__,_|\___|\__\__,_|\___\___|\__,_| \_____/\__,_|\___|_|\_\___|_| |_|\__,_|
  """  ) // scalastyle:ignore

  println("") // scalastyle:ignore
  println(s"db.master.user = ${db.master.user}") // scalastyle:ignore
  println(s"db.master.password = ${db.master.password}") // scalastyle:ignore
  println(s"db.master.database = ${db.master.database}") // scalastyle:ignore
  println(s"db.master.dest = ${db.master.dest}") // scalastyle:ignore
  println(s"db.master.lowWatermark = ${db.master.lowWatermark}") // scalastyle:ignore
  println(s"db.master.highWatermark = ${db.master.highWatermark}") // scalastyle:ignore
  println(s"db.master.idleTime = ${db.master.idleTime}") // scalastyle:ignore
  println(s"db.master.bufferSize = ${db.master.bufferSize}") // scalastyle:ignore
  println(s"db.master.maxPrepareStatements = ${db.master.maxPrepareStatements}") // scalastyle:ignore
  println(s"db.master.connectTimeout = ${db.master.connectTimeout}") // scalastyle:ignore
  println(s"db.master.maxWaiters = ${db.master.maxWaiters}") // scalastyle:ignore
  if (db.useSlave) {
    println(s"db.slave.user = ${db.slave.user}") // scalastyle:ignore
    println(s"db.slave.password = ${db.slave.password}") // scalastyle:ignore
    println(s"db.slave.database = ${db.slave.database}") // scalastyle:ignore
    println(s"db.slave.dest = ${db.slave.dest}") // scalastyle:ignore
    println(s"db.slave.lowWatermark = ${db.slave.lowWatermark}") // scalastyle:ignore
    println(s"db.slave.highWatermark = ${db.slave.highWatermark}") // scalastyle:ignore
    println(s"db.slave.idleTime = ${db.slave.idleTime}") // scalastyle:ignore
    println(s"db.slave.bufferSize = ${db.slave.bufferSize}") // scalastyle:ignore
    println(s"db.slave.maxPrepareStatements = ${db.slave.maxPrepareStatements}") // scalastyle:ignore
    println(s"db.slave.connectTimeout = ${db.slave.connectTimeout}") // scalastyle:ignore
    println(s"db.slave.maxWaiters = ${db.slave.maxWaiters}") // scalastyle:ignore
  }
  println(s"auth.apiKeyHeaderName = ${auth.headerNames.apiKey}") // scalastyle:ignore
  println(s"auth.authorizationHeaderName = ${auth.headerNames.authorizationKey}") // scalastyle:ignore
  println(s"auth.iosApiKey = ${auth.keys.ios}") // scalastyle:ignore
  println(s"auth.androidApiKey = ${auth.keys.android}") // scalastyle:ignore
  println(s"auth.webApiKey = ${auth.keys.web}") // scalastyle:ignore
  println(s"auth.signingKey = ${auth.token.signingKey}") // scalastyle:ignore
  println(s"auth.algorithm = ${auth.token.algorithm}") // scalastyle:ignore
  println(s"auth.expire = ${auth.token.expire}") // scalastyle:ignore
  println(s"auth.issuer = ${auth.token.issuer}") // scalastyle:ignore
  println(s"auth.subject = ${auth.token.subject}") // scalastyle:ignore
  println(s"password.salt = ${password.salt}") // scalastyle:ignore
  println(s"password.iterations = ${password.iterations}") // scalastyle:ignore
  println(s"password.keyLength = ${password.keyLength}") // scalastyle:ignore
  println(s"crypter.crypterKey = ${crypter.crypterKey}") // scalastyle:ignore
  println(s"crypter.signerKey = ${crypter.signerKey}") // scalastyle:ignore
  println(s"facebook.authorizationURL = ${facebook.authorizationURL}") // scalastyle:ignore
  println(s"facebook.accessTokenURL = ${facebook.accessTokenURL}") // scalastyle:ignore
  println(s"facebook.redirectURL = ${facebook.redirectURL}") // scalastyle:ignore
  println(s"facebook.clientID = ${facebook.clientID}") // scalastyle:ignore
  println(s"facebook.clientSecret = ${facebook.clientSecret}") // scalastyle:ignore
  println(s"facebook.scope = ${facebook.scope}") // scalastyle:ignore
  println(s"google.authorizationURL = ${google.authorizationURL}") // scalastyle:ignore
  println(s"google.accessTokenURL = ${google.accessTokenURL}") // scalastyle:ignore
  println(s"google.redirectURL = ${google.redirectURL}") // scalastyle:ignore
  println(s"google.clientID = ${google.clientID}") // scalastyle:ignore
  println(s"google.clientSecret = ${google.clientSecret}") // scalastyle:ignore
  println(s"google.scope = ${google.scope}") // scalastyle:ignore
  println(s"twitter.requestTokenURL = ${twitter.requestTokenURL}") // scalastyle:ignore
  println(s"twitter.accessTokenURL = ${twitter.accessTokenURL}") // scalastyle:ignore
  println(s"twitter.authorizationURL = ${twitter.authorizationURL}") // scalastyle:ignore
  println(s"twitter.callbackURL = ${twitter.callbackURL}") // scalastyle:ignore
  println(s"twitter.consumerKey = ${twitter.consumerKey}") // scalastyle:ignore
  println(s"twitter.consumerSecret = ${twitter.consumerSecret}") // scalastyle:ignore
  println("") // scalastyle:ignore

}
