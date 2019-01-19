package io.github.cactacea.backend.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ChatService
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.github.cactacea.finachat.{PublishService, Response}

class DefaultChatService extends ChatService {

  override def publish(groupId: GroupId, message: AnyRef): Future[Unit] = {
    val response = Response.messageArrived(Some(message))
    PublishService.publish(groupId.toString(), response).flatMap(_ => Future.Done)
  }

}
