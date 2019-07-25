package io.github.cactacea.backend.server

import com.twitter.finagle.Service
import io.github.cactacea.finachat.{ChatServer => BaseChatServer}
import io.github.cactacea.finasocket.Client

class ChatServer extends BaseChatServer {

  override def chatService: Service[Client, Client] = {
    new ChatService()
  }

}
