package io.github.cactacea.finachat

import com.twitter.finagle.Service
import com.twitter.inject.annotations.Lifecycle
import io.github.cactacea.finasocket.Client

trait ChatServer extends BaseChatServer {

  @Lifecycle
  override protected def postInjectorStartup(): Unit = {
    super.postInjectorStartup()
  }

  /* Overrides */

  override final def chatService: Service[Client, Client] = {
    val handler = new DefaultChatHandler()
    new ChatService(handler)
  }

}