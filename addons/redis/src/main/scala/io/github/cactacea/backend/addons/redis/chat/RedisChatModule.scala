package io.github.cactacea.backend.addons.redis.chat

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.ChatService

object RedisChatModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[ChatService].to(classOf[RedisChatService])
  }
}
