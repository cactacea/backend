package io.github.cactacea.addons.onesignal.mobilepush

import com.twitter.inject.TwitterModule
import io.github.cactacea.addons.onesignal.utils.OneSignalClientModule
import io.github.cactacea.backend.core.application.components.interfaces.MobilePushService

object OneSignalMobilePushModule extends TwitterModule {

  override val modules = Seq(
    OneSignalClientModule
  )

  override def configure(): Unit = {
    bindSingleton[MobilePushService].to(classOf[OneSignalMobilePushService])
  }
}
