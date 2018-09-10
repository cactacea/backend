package io.github.cactacea.backend.externals.infrastructure.pushnotifications.onesignal

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.PushNotificationService

object OneSignalModule extends TwitterModule {

  override val modules = Seq(OneSignalClientModule)

  override def configure() {
    bindSingleton[PushNotificationService].to(classOf[OneSignalService])
  }

}
