package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.DequeueService
import io.github.cactacea.backend.core.application.components.services.DefaultDequeueService

object DefaultSubScribeModule extends TwitterModule {

  override def configure() {
    bindSingleton[DequeueService].to[DefaultDequeueService]
  }

}
