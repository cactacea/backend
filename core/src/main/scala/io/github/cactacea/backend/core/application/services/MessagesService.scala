package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{ListenerService, QueueService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.domain.repositories.MessagesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers._

class MessagesService @Inject()(
                                 db: DatabaseService,
                                 messagesRepository: MessagesRepository,
                                 queueService: QueueService,
                                 listenerService: ListenerService
                               ) {

  def createText(groupId: GroupId, message: String, sessionId: SessionId): Future[Message] = {
    for {
      m <- db.transaction(messagesRepository.createText(groupId, message, sessionId))
      _ <- queueService.enqueueMessage(m.id)
      _ <- listenerService.messageCreated(m.id, groupId, Some(message), None, sessionId)
    } yield (m)
  }

  def createMedium(groupId: GroupId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    for {
      m <- db.transaction(messagesRepository.createMedium(groupId, mediumId, sessionId))
      _ <- queueService.enqueueMessage(m.id)
      _ <- listenerService.messageCreated(m.id, groupId, None, Some(mediumId), sessionId)
    } yield (m)
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(messagesRepository.delete(groupId, sessionId))
      _ <- listenerService.messagesDeleted(groupId, sessionId)
    } yield (Unit)
  }

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, ascending: Boolean, sessionId: SessionId): Future[List[Message]] = {
    db.transaction {
      for {
        m <- messagesRepository.find(groupId, since, offset, count, ascending, sessionId)
        _ <- messagesRepository.updateReadStatus(m, sessionId)
      } yield (m)
    }
  }

}
