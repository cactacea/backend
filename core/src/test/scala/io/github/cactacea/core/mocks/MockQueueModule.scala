package io.github.cactacea.core.mocks

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.infrastructure.services.QueueService

object MockQueueModule extends TwitterModule {

  override def configure() {
    bindSingleton[QueueService].to(classOf[MockQueueService])
  }

}
