package io.github.cactacea.backend

import com.twitter.finagle.Service
import io.github.cactacea.backend.core.application.components.modules.BaseModules
import io.github.cactacea.finachat.{ChatServer => BaseChatServer}
import io.github.cactacea.finasocket.Client

class ChatServer extends BaseChatServer with BaseModules {

  override def chatService: Service[Client, Client] = {
    new ChatService()
  }

}
