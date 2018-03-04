package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, PublishService}
import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.domain.repositories.MessagesRepository
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.DatabaseService

class MessagesService @Inject()(db: DatabaseService, messagesRepository: MessagesRepository, publishService: PublishService, injectionService: InjectionService) {

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    db.transaction {
      for {
        id <- db.transaction(messagesRepository.create(groupId, message, mediumId, sessionId))
        _ <- publishService.enqueueMessage(id)
        _ <- injectionService.messageCreated(id, sessionId)
      } yield (id)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- injectionService.messagesDeleted(groupId, sessionId)
      _ <- db.transaction(messagesRepository.delete(groupId, sessionId).flatMap(_ => Future.Unit))
    } yield (r)
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
