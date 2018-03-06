package io.github.cactacea.core.infrastructure.services

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.typesafe.config.ConfigFactory

object DatabaseProviderModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    val config = ConfigFactory.load("application.conf").getConfig("db.master")
    new DatabaseService(config)
  }

}
