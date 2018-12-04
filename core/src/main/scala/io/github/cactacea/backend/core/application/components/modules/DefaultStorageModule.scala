package io.github.cactacea.backend.core.application.components.modules

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.components.interfaces.StorageService
import io.github.cactacea.backend.core.application.components.services.DefaultStorageService

// Do not use this service with default settings.

object DefaultStorageModule extends TwitterModule {

  flag(name = "storage.localPath", default = "/tmp/", "local volume path to store uploaded user's media.")

  override def configure() {
    bindSingleton[StorageService].to(classOf[DefaultStorageService])
  }

  @Singleton
  @Provides
  def provide(@Flag("storage.localPath") localPath: String): DefaultStorageService = {
    new DefaultStorageService(localPath)
  }

}
