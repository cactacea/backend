package io.github.cactacea.backend.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ChatService
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.github.cactacea.finachat.PublishService

class DefaultChatService extends ChatService {

  override def publish(groupId: GroupId, message: String): Future[Unit] = {
    PublishService.publish(groupId.toString(), message).flatMap(_ => Future.Done)
  }

}
