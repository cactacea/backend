package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.PushNotificationService
import io.github.cactacea.core.application.components.services.NoPushNotificationService

object NoPushNotificationModule extends TwitterModule {

  override def configure() {
    bindSingleton[PushNotificationService].to[NoPushNotificationService]
  }

}
