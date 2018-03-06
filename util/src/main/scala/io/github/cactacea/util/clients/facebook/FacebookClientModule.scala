package io.github.cactacea.util.clients.facebook

import com.twitter.inject.TwitterModule

object FacebookClientModule extends TwitterModule {

  override val modules = Seq(
    FacebookHttpClientModule
  )

  override def configure(): Unit = {
    bindSingleton[FacebookClient]
  }
}
