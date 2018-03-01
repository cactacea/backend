package io.github.cactacea.core.helpers

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.interfaces.QueueService

object MockQueueModule extends TwitterModule {

  override def configure() {
    bindSingleton[QueueService].to(classOf[MockQueueService])
  }

}
