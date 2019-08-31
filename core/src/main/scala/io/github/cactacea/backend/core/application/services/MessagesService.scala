package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.domain.repositories.MessagesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers._

class MessagesService @Inject()(
                                 databaseService: DatabaseService,
                                 messagesRepository: MessagesRepository,
                                 queueService: QueueService
                               ) {

  import databaseService._

  def createText(groupId: GroupId, message: String, sessionId: SessionId): Future[Message] = {
    transaction {
      for {
        m <- messagesRepository.createText(groupId, message, sessionId)
        _ <- queueService.enqueueMessage(m.id)
      } yield (m)
    }
  }

  def createMedium(groupId: GroupId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    transaction {
      for {
        m <- messagesRepository.createMedium(groupId, mediumId, sessionId)
        _ <- queueService.enqueueMessage(m.id)
      } yield (m)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction {
      messagesRepository.delete(groupId, sessionId)
    }
  }

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, ascending: Boolean, sessionId: SessionId): Future[List[Message]] = {
    transaction {
      for {
        m <- messagesRepository.find(groupId, since, offset, count, ascending, sessionId)
      } yield (m)
    }
  }

}
