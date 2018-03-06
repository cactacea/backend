package io.github.cactacea.util.clients.twitter

import com.twitter.inject.TwitterModule

object TwitterClientModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[TwitterClient]
  }

}

