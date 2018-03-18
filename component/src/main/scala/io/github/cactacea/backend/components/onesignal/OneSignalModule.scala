package io.github.cactacea.backend.components.onesignal

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.PushNotificationService

object OneSignalModule extends TwitterModule {

  override val modules = Seq(OneSignalClientModule)

  override def configure() {
    bindSingleton[PushNotificationService].to(classOf[OneSignalService])
  }

}
