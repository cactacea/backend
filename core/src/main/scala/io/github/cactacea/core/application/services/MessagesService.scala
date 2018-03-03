package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.PublishService
import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.domain.repositories.{MessagesRepository}
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.DatabaseService

class MessagesService @Inject()(db: DatabaseService) {

  @Inject var messagesRepository: MessagesRepository = _
  @Inject var publishService: PublishService = _

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    db.transaction {
      for {
        id <- db.transaction(messagesRepository.create(groupId, message, mediumId, sessionId))
        _ <- publishService.enqueueMessage(id)
      } yield (id)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      messagesRepository.delete(groupId, sessionId).flatMap(_ => Future.Unit)
    }
  }

  def find(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], ascending: Boolean, sessionId: SessionId): Future[List[Message]] = {
    db.transaction {
      for {
        m <- messagesRepository.findAll(groupId, since, offset, count, ascending, sessionId)
        _ <- messagesRepository.updateReadStatus(m, sessionId)
      } yield (m)
    }
  }

}
