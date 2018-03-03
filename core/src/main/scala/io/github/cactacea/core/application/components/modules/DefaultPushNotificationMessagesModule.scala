package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.PushNotificationMessagesService
import io.github.cactacea.core.application.components.services.DefaultPushNotificationMessagesService

object DefaultPushNotificationMessagesModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[PushNotificationMessagesService].to(classOf[DefaultPushNotificationMessagesService])
  }

}
