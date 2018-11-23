package io.github.cactacea.backend.utils.warmups

import com.google.inject.Singleton
import com.twitter.inject.utils.Handler

@Singleton
class DatabaseMigrationHandler extends Handler {

  override def handle() = {
    DatabaseMigration.execute()
  }

}