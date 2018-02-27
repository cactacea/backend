package io.github.cactacea.backend.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.services.DefaultQueueService
import io.github.cactacea.core.infrastructure.services.QueueService

object DefaultQueueModule extends TwitterModule {

  override def configure() {
    bindSingleton[QueueService].to(classOf[DefaultQueueService])
  }

}
