package io.github.cactacea.backend.externals.configs


import com.typesafe.config.{ConfigFactory, Config => TypeSafeConfig}
import io.github.cactacea.backend.core.util.configs.DurationReader
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object Config extends DurationReader {

  private val config = ConfigFactory.load().as[TypeSafeConfig]("cactacea")
  private val facebookConfig = config.as[FacebookConfig]("facebook")
  private val googleConfig = config.as[GoogleConfig]("google")
  private val s3Config = config.as[S3Config]("s3")
  private val onesignalConfig = config.as[OneSignalConfig]("onesignal")
  private val twitterConfig = config.as[TwitterConfig]("twitter")

  object facebook {
    lazy val apiKey = facebookConfig.apiKey.getOrElse("")
  }

  object google {
    lazy val apiKey = googleConfig.apiKey.getOrElse("")
  }

  object s3 {
    lazy val awsAccessKeyId = s3Config.bucketName.getOrElse("EKD4SWF0SAENSZ3Z1CFA") // for Minio service
    lazy val awsSecretAccessKey = s3Config.bucketName.getOrElse("n7ynf2m/rheYuPWoLwK2b+pZTGQ7RBZDEhwOBYzx") // for Minio service
    lazy val bucketName = s3Config.bucketName.getOrElse("cactacea")
    lazy val endpoint = s3Config.endpoint.getOrElse("http://127.0.0.1:8000")
  }

  object onesignal {
    lazy val apiKey = onesignalConfig.apiKey.getOrElse("")
    lazy val appId = onesignalConfig.appId.getOrElse("")
  }

  object twitter {
    lazy val tokenKey = twitterConfig.tokenKey.getOrElse("")
    lazy val tokenSecret = twitterConfig.tokenSecret.getOrElse("")
  }

  println("____ ___  ____ ____ ___  ____ ____ ___  ")
  println("| __\\|  \\ | __\\|_ _\\|  \\ | __\\| __\\|  \\ ")
  println("| \\__| . \\| \\__  || | . \\| \\__|  ]_| . \\")
  println("|___/|/\\_/|___/  |/ |/\\_/|___/|___/|/\\_/")
  println("")

  println("---------------------------------------------------------------")
  println(s"facebook.apiKey = ${facebook.apiKey}")
  println(s"google.apiKey = ${google.apiKey}")
  println(s"s3.awsAccessKeyId = ${s3.awsAccessKeyId}")
  println(s"s3.awsSecretAccessKey = ${s3.awsSecretAccessKey}")
  println(s"s3.bucketName = ${s3.bucketName}")
  println(s"s3.endpoint = ${s3.endpoint}")
  println(s"onesignal.apiKey = ${onesignal.apiKey}")
  println(s"onesignal.appId = ${onesignal.appId}")
  println(s"twitter.tokenKey = ${twitter.tokenKey}")
  println(s"twitter.tokenSecret = ${twitter.tokenSecret}")
  println("---------------------------------------------------------------")
  println("")

}

