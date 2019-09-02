package com.twitter.finatra.http.benchmarks

import com.twitter.finagle.stats.{NullStatsReceiver, StatsReceiver}
import com.twitter.inject.TwitterModule

object NullStatsReceiverModule extends TwitterModule {
  override def configure(): Unit = {
    bindSingleton[StatsReceiver].toInstance(NullStatsReceiver)
  }
}