package io.github.cactacea.backend.helpers

import java.nio.file.{Files, Paths}

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.components.interfaces.StorageService

object DemoStorageModule extends TwitterModule {

  flag(name = "storage.localPath", default = "src/main/resources/demo/images/", "local volume path to store uploaded user's media.")

  override def configure() {
    bindSingleton[StorageService].to(classOf[DemoStorageService])
  }

  @Singleton
  @Provides
  def provide(@Flag("storage.localPath") localPath: String): DemoStorageService = {
    val path = Paths.get(localPath)
    if (!Files.exists(path)) {
      Files.createDirectory(path)
    }
    new DemoStorageService(localPath)
  }

}
