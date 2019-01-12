package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DefaultQueueService

object DefaultQueueModule extends TwitterModule {

  override def configure() {
    bindSingleton[QueueService].to[DefaultQueueService]
  }

}
