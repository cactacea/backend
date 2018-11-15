package io.github.cactacea.backend.core.application.components.modules

import java.util.TimeZone

import com.google.inject.{Provides, Singleton}
import com.twitter.finagle.client.DefaultPool
import com.twitter.finagle.{Mysql, mysql}
import com.twitter.inject.TwitterModule
import com.twitter.conversions.time._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.util.configs.Config

object DatabaseModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    Config.db.useSlave match {
      case true =>
        val master = create(Config.db.master)
        val slave = create(Config.db.slave)
        new DatabaseService(master, slave, TimeZone.getDefault)
      case false =>
        val master = create(Config.db.master)
        new DatabaseService(master, TimeZone.getDefault)
    }
  }

  def create(config: Config.db.config): mysql.Client with mysql.Transactions = {
    val client = Mysql.client
    .withCredentials(config.user, config.password)
    .withDatabase(config.database)
    .withMaxConcurrentPrepareStatements(config.maxPrepareStatements)
    .withTransport
    .connectTimeout(config.connectTimeout.second)
      .configured(DefaultPool.Param(
        low = config.lowWatermark, high = config.highWatermark,
        idleTime = config.idleTime,
        bufferSize = config.bufferSize,
        maxWaiters = config.maxWaiters
      ))
    client.newRichClient(config.dest)
  }


}

