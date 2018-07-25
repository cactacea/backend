package io.github.cactacea.backend.utils.clients.google

import com.twitter.inject.TwitterModule

object GoogleClientModule extends TwitterModule {

  override val modules = Seq(
    GoogleHttpClientModule
  )

  override def configure(): Unit = {
    bindSingleton[GoogleClient]
  }
}

