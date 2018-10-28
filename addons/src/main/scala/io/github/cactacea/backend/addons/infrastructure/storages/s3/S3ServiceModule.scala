package io.github.cactacea.backend.addons.infrastructure.storages.s3

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.StorageService

object S3ServiceModule extends TwitterModule {

  override def configure() {
    bindSingleton[StorageService].to(classOf[S3Service])
  }

}