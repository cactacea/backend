package io.github.cactacea.core.mocks

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.infrastructure.services.PushNotificationService

object MockPushNotificationModule extends TwitterModule {

  override def configure() {
    bindSingleton[PushNotificationService].to(classOf[MockPushNotificationService])
  }

}
