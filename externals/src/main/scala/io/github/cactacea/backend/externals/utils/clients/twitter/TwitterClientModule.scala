package io.github.cactacea.backend.externals.utils.clients.twitter

import com.twitter.inject.TwitterModule

object TwitterClientModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[TwitterClient]
  }

}

