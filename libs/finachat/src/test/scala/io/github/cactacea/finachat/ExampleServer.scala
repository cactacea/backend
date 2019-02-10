package io.github.cactacea.finachat

import com.twitter.finagle.Service
import io.github.cactacea.finasocket.Client

class ExampleServer extends ChatServer {

  override def chatService: Service[Client, Client] = {
    val handler = new ExampleChatHandler()
    new ChatService(handler)
  }

}
