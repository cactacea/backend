package io.github.cactacea.backend.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.services.{DefaultStorageService}
import io.github.cactacea.core.infrastructure.services.StorageService


object DefaultStorageModule extends TwitterModule {

  override def configure() {
    bindSingleton[StorageService].to(classOf[DefaultStorageService])
  }

}
