package io.github.cactacea.backend.util.warmups

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.routing.HttpWarmup
import com.twitter.inject.utils.Handler
import io.github.cactacea.backend.core.infrastructure.DatabaseMigration

@Singleton
class DatabaseMigrationHandler @Inject()(httpWarmup: HttpWarmup) extends Handler {

  override def handle() = {
    DatabaseMigration.execute()
  }

}