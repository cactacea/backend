package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.FanOutService
import io.github.cactacea.core.application.components.services.DefaultFanOutService

object DefaultFanOutModule extends TwitterModule {

  override def configure() {
    bindSingleton[FanOutService].to(classOf[DefaultFanOutService])
  }

}
