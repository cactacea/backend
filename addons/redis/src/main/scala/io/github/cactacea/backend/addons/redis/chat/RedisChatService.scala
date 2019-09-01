package io.github.cactacea.backend.addons.redis.chat

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ChatService
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.github.cactacea.finachat.{PublishService, Response}

class RedisChatService extends ChatService {

  override def publish(channelId: ChannelId, message: AnyRef): Future[Unit] = {
    val response = Response.messageArrived(Some(message))
    PublishService.publish(channelId.toString(), response).flatMap(_ => Future.Done)
  }

}
