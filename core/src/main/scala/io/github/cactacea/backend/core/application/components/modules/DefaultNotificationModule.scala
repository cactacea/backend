package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.NotificationService
import io.github.cactacea.backend.core.application.components.services.DefaultNotificationService

object DefaultNotificationModule extends TwitterModule {

  override def configure() {
    bindSingleton[NotificationService].to[DefaultNotificationService]
  }

}
