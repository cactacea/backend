package io.github.cactacea.backend.core.application.components.modules

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.typesafe.config.ConfigFactory
import io.github.cactacea.backend.core.application.components.services.DatabaseService

object DatabaseProviderModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    val config = ConfigFactory.load("application.conf").getConfig("db.master")
    new DatabaseService(config)
  }

}
