package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.SubScribeService
import io.github.cactacea.core.application.components.services.NoQueueSubScribeService

object NoQueueSubScribeModule extends TwitterModule {

  override def configure() {
    bindSingleton[SubScribeService].to[NoQueueSubScribeService]
  }

}
