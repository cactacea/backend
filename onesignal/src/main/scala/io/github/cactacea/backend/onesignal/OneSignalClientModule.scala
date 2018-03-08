package io.github.cactacea.backend.onesignal

import com.twitter.inject.TwitterModule

object OneSignalClientModule extends TwitterModule {

  override val modules = Seq(
    OneSignalHttpClientModule
  )

  override def configure(): Unit = {
    bindSingleton[OneSignalClient]
  }

}

