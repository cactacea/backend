package io.github.cactacea.addons.onesignal.utils

import com.typesafe.config.{ConfigFactory, Config => TypeSafeConfig}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

case class OneSignalConfig (
                             apiKey: Option[String],
                             appId: Option[String]
                           )

object OneSignalConfig {

  private val config = ConfigFactory.load().as[TypeSafeConfig]
  private val onesignalConfig = config.as[OneSignalConfig]("onesignal")

  object onesignal { // scalastyle:ignore
    lazy val apiKey = onesignalConfig.apiKey.getOrElse("must be ONESIGNAL_API_KEY")
    lazy val appId = onesignalConfig.appId.getOrElse("must be ONESIGNAL_APP_ID")
  }

  println("---------------------------------------------------------------")
  println(s"onesignal.apiKey = ${onesignal.apiKey}")
  println(s"onesignal.appId = ${onesignal.appId}")
  println("---------------------------------------------------------------")
  println("")

}

