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

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    db.transaction {
      for {
        id <- messagesRepository.create(groupId, message, mediumId, sessionId)
        _ <- publishService.enqueueMessage(id)
        _ <- injectionService.messageCreated(id, groupId, message, mediumId, sessionId)
      } yield (id)
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
        m <- messagesRepository.findAll(groupId, since, offset, count, ascending, sessionId)
        _ <- messagesRepository.updateReadStatus(m, sessionId)
      } yield (m)
    }
  }

}
