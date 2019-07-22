package io.github.cactacea.backend.server.utils.warmups

import com.google.inject.Singleton
import com.twitter.inject.utils.Handler

@Singleton
class CactaceaDatabaseMigrationHandler extends Handler {

  override def handle(): Unit = {
    CactaceaDatabaseMigration.execute()
  }

}
