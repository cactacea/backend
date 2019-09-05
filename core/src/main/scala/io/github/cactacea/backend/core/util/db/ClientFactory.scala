package io.github.cactacea.backend.core.util.db

import com.twitter.conversions.DurationOps._
import com.twitter.finagle.client.DefaultPool
import com.twitter.finagle.{Mysql, mysql}
import io.github.cactacea.backend.core.util.configs.Config

object ClientFactory {

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
