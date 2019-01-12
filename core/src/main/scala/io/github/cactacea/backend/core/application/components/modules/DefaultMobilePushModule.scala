package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.MobilePushService
import io.github.cactacea.backend.core.application.components.services.DefaultMobilePushService

object DefaultMobilePushModule extends TwitterModule {

  override def configure() {
    bindSingleton[MobilePushService].to[DefaultMobilePushService]
  }

}
