package io.github.cactacea.core.infrastructure.services

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule

object DatabaseProviderModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    new DatabaseService("db.master")
  }

}
