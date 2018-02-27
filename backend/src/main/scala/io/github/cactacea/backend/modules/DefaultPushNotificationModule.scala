package io.github.cactacea.backend.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.services.DefaultPushNotificationService
import io.github.cactacea.core.infrastructure.services.PushNotificationService

object DefaultPushNotificationModule extends TwitterModule {

  override def configure() {
    bindSingleton[PushNotificationService].to(classOf[DefaultPushNotificationService])
  }

}
