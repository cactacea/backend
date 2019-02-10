package io.github.cactacea.addons.aws.storage

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.StorageService

object AWSS3StorageModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[StorageService].to(classOf[AWSS3StorageService])
  }
}
