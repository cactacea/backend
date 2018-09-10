package io.github.cactacea.backend.utils.warmups

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.routing.HttpWarmup
import com.twitter.inject.utils.Handler

@Singleton
class DatabaseMigrationHandler @Inject()(httpWarmup: HttpWarmup) extends Handler {

  override def handle() = {
    DatabaseMigration.execute()
  }

}