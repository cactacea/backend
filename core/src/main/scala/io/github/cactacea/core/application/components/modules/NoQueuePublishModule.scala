package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.PublishService
import io.github.cactacea.core.application.components.services.NoQueuePublishService

object NoQueuePublishModule extends TwitterModule {

  override def configure() {
    bindSingleton[PublishService].to[NoQueuePublishService]
  }

}
