package io.github.cactacea.backend.core.application.components.modules

import java.util.TimeZone

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.db.ClientFactory

object DatabaseModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    Config.db.useSlave match {
      case true =>
        val master = ClientFactory.create(Config.db.master)
        val slave = ClientFactory.create(Config.db.slave)
        new DatabaseService(master, slave, TimeZone.getDefault)
      case false =>
        val master = ClientFactory.create(Config.db.master)
        new DatabaseService(master, TimeZone.getDefault)
    }
  }

}

