package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.EnqueueService
import io.github.cactacea.backend.core.application.components.services.DefaultEnqueueService

object DefaultPublishModule extends TwitterModule {

  override def configure() {
    bindSingleton[EnqueueService].to[DefaultEnqueueService]
  }

}
