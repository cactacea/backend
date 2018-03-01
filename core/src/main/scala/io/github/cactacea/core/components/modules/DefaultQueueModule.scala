package io.github.cactacea.core.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.interfaces.QueueService
import io.github.cactacea.core.components.services.DefaultQueueService

object DefaultQueueModule extends TwitterModule {

  override def configure() {
    bindSingleton[QueueService].to(classOf[DefaultQueueService])
  }

}
