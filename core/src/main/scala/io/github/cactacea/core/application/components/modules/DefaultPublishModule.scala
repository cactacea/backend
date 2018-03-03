package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.PublishService
import io.github.cactacea.core.application.components.services.DefaultPublishService

object DefaultPublishModule extends TwitterModule {

  override def configure() {
    bindSingleton[PublishService].to(classOf[DefaultPublishService])
  }

}
