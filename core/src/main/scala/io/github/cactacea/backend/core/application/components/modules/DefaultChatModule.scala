package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.ChatService
import io.github.cactacea.backend.core.application.components.services.DefaultChatService

object DefaultChatModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[ChatService].to[DefaultChatService]
  }
}
