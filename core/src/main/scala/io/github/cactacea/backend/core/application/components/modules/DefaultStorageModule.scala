package io.github.cactacea.backend.core.application.components.modules

import java.nio.file.{Files, Paths}

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.components.interfaces.StorageService
import io.github.cactacea.backend.core.application.components.services.DefaultStorageService

// Do not use for production service.

object DefaultStorageModule extends TwitterModule {

  flag(name = "storage.localPath", default = "/tmp/io.github.cactacea.backend.demo/", "local volume path to store uploaded user's media.")

  override def configure() {
    bindSingleton[StorageService].to(classOf[DefaultStorageService])
  }

  @Singleton
  @Provides
  def provide(@Flag("storage.localPath") localPath: String): DefaultStorageService = {
    val path = Paths.get(localPath)
    if (!Files.exists(path)) {
      Files.createDirectory(path)
    }
    new DefaultStorageService(localPath)
  }

}
