package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.FanOutService
import io.github.cactacea.backend.core.application.components.services.DefaultFanOutService

object DefaultFanOutModule extends TwitterModule {

  override def configure() {
    bindSingleton[FanOutService].to[DefaultFanOutService]
  }

}
