package io.github.cactacea.backend.core.application.components.modules

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.typesafe.config.ConfigFactory
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.util.configs.Config

import scala.collection.JavaConverters._

object DatabaseProviderModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    val map = Map(
      "user" -> Config.db.user,
      "password" -> Config.db.password,
      "database" -> Config.db.database,
      "dest" -> Config.db.dest,
      "pool.watermark.low" -> Config.db.poolWatermarkLow,
      "pool.watermark.max" -> Config.db.poolWatermarkMax
    ).asJava
    val config = ConfigFactory.parseMap(map)
    new DatabaseService(config)
  }

}
