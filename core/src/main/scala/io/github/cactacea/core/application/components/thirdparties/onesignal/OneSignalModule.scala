package io.github.cactacea.core.application.components.thirdparties.onesignal

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.PushNotificationService
import io.github.cactacea.core.application.components.services.DefaultPushNotificationService
import io.github.cactacea.util.clients.onesignal.OneSignalClientModule

object OneSignalModule extends TwitterModule {

  override val modules = Seq(OneSignalClientModule)

  override def configure() {
    bindSingleton[PushNotificationService].to(classOf[DefaultPushNotificationService])
  }

}
