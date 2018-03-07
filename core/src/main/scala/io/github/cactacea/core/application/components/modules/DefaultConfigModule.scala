package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import com.typesafe.config.ConfigFactory
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.components.services.DefaultConfigService

object DefaultConfigModule extends TwitterModule {

  private lazy val config = ConfigFactory.load("application.conf")
  private lazy val auth = config.getConfig("auth")
  private lazy val apiKey = auth.getString("apiKey")
  private lazy val signingKey = auth.getString("signingKey")
  private lazy val expire = auth.getLong("expire")
  private lazy val issuer = auth.getString("issuer")
  private lazy val subject = auth.getString("subject")
  private lazy val algorithm = auth.getString("algorithm")

  private val maximumGroupAccountLimits = flag(name = "maximumGroupAccountLimits", default = "0", help = "Number of max account count per group")
  private val basePointInTime = flag("basePointInTime", default = "978307200", help = "Default value is # 2001/01/01 00:00:00")

  override def configure(): Unit = {
    val max = maximumGroupAccountLimits.get.map(_.toLong).getOrElse(0L)
    val time = 978307200 //pointInTime.get.map(_.toLong).getOrElse(0L)
    val service = new DefaultConfigService(apiKey, signingKey, expire, issuer, subject, algorithm, max, time)
    bindSingleton[ConfigService].toInstance(service)
  }

}
