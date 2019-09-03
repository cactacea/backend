package io.github.cactacea.backend.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ChatService
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId

class DefaultChatService extends ChatService {

  override def publish(channelId: ChannelId, message: AnyRef): Future[Unit] = {
    println("----- Chat Message ----") // scalastyle:ignore
    println(message) // scalastyle:ignore
    Future.Done
  }

}
