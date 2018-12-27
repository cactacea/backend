package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{InjectionService, EnqueueService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.domain.repositories.MessagesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers._

class MessagesService @Inject()(
                                 db: DatabaseService,
                                 messagesRepository: MessagesRepository,
                                 publishService: EnqueueService,
                                 injectionService: InjectionService
                               ) {

  def createText(groupId: GroupId, message: String, sessionId: SessionId): Future[Message] = {
    db.transaction {
      for {
        m <- messagesRepository.createText(groupId, message, sessionId)
        _ <- publishService.enqueueMessage(m.id)
        _ <- injectionService.messageCreated(m.id, groupId, Some(message), None, sessionId)
      } yield (m)
    }
  }

  def createMedium(groupId: GroupId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    db.transaction {
      for {
        m <- messagesRepository.createMedium(groupId, mediumId, sessionId)
        _ <- publishService.enqueueMessage(m.id)
        _ <- injectionService.messageCreated(m.id, groupId, None, Some(mediumId), sessionId)
      } yield (m)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- injectionService.messagesDeleted(groupId, sessionId)
        _ <- messagesRepository.delete(groupId, sessionId)
      } yield (Unit)
    }
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
