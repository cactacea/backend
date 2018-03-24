package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.PushNotificationService
import io.github.cactacea.backend.core.application.components.services.DefaultPushNotificationService

object DefaultPushNotificationModule extends TwitterModule {

  override def configure() {
    bindSingleton[PushNotificationService].to[DefaultPushNotificationService]
  }

}
