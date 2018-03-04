package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.NotificationMessagesService
import io.github.cactacea.core.application.components.services.DefaultNotificationMessagesService

object DefaultNotificationMessagesModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[NotificationMessagesService].to(classOf[DefaultNotificationMessagesService])
  }

}
