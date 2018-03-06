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

  private val maxGroupAccountsCount = flag(name = "maxGroupAccountsCount", default = "0", help = "auth token expire")

  override def configure(): Unit = {
    val max = maxGroupAccountsCount.get.map(_.toLong).getOrElse(0L)
    val service = new DefaultConfigService(apiKey, signingKey, expire, issuer, subject, algorithm, max)
    bindSingleton[ConfigService].toInstance(service)
  }

}
