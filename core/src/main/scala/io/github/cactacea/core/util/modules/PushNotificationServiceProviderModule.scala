package io.github.cactacea.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.infrastructure.pushnotifications.{NoPushNotificationService, OneSignalService, PushNotificationService}

object PushNotificationServiceProviderModule extends TwitterModule {

  val env = flag(name = "push_notification", default = "local", help = "local or one_signal")

  override def configure() {
    env() match {
      case "one_signal" =>
        bindSingleton[PushNotificationService].to(classOf[OneSignalService])
      case _ =>
        bindSingleton[PushNotificationService].to(classOf[NoPushNotificationService])
    }
  }

}
