package io.github.cactacea.core.application.components.modules

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.twitter.inject.annotations.Flag
import io.github.cactacea.core.application.components.interfaces.StorageService
import io.github.cactacea.core.application.components.services.DefaultStorageService

object DefaultStorageModule extends TwitterModule {

  flag(name = "storage.host", default = "http://localhost:9000", help = "endpoint host to access uploaded user's media.")
  flag(name = "storage.path", default = "/mediums", help = "endpoint path to access uploaded user's media.")
  flag(name = "storage.localPath", default = "", "local volume path to store uploaded user's media.")

  override def configure() {
    bindSingleton[StorageService].to(classOf[DefaultStorageService])
  }

  @Singleton
  @Provides
  def provide(@Flag("storage.host") host: String, @Flag("storage.path") path: String, @Flag("storage.localPath") localPath: String): DefaultStorageService = {
    val endpoint = host + path
    new DefaultStorageService(endpoint, localPath)
  }

}
