package io.github.cactacea.core.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.interfaces.PushNotificationService
import io.github.cactacea.core.components.services.DefaultPushNotificationService

object DefaultPushNotificationModule extends TwitterModule {

  override def configure() {
    bindSingleton[PushNotificationService].to(classOf[DefaultPushNotificationService])
  }

}
