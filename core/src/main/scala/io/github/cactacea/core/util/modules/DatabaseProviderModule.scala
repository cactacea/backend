package io.github.cactacea.core.util.provider.module

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import io.github.cactacea.core.infrastructure.db.DatabaseService

object DatabaseProviderModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    new DatabaseService("db.master")
  }

}
