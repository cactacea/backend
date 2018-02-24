package io.github.cactacea.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.infrastructure.storages.{S3Service, StorageService}

object StorageServiceProviderModule extends TwitterModule {

  val env = flag(name = "storage", default = "s3", help = "currently support only S3")

  override def configure() {
    env() match {
      case _ =>
        bindSingleton[StorageService].to(classOf[S3Service])
    }
  }

}
