package io.github.cactacea.core.helpers

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.interfaces.PushNotificationService

object MockPushNotificationModule extends TwitterModule {

  override def configure() {
    bindSingleton[PushNotificationService].to(classOf[MockPushNotificationService])
  }

}
