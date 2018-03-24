package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DefaultDeepLinkService

object DefaultDeepLinkModule extends TwitterModule {

  flag(name = "scheme", default = "cactacea", help = "Deep link scheme name. default value is cactacea.")

  override def configure() = {
    bindSingleton[DeepLinkService].to[DefaultDeepLinkService]
  }


}
