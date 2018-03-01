package io.github.cactacea.core.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.interfaces.StorageService
import io.github.cactacea.core.components.services.DefaultStorageService


object DefaultStorageModule extends TwitterModule {

  override def configure() {
    bindSingleton[StorageService].to(classOf[DefaultStorageService])
  }

}
