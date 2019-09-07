package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.domain.repositories.MessagesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class MessagesService @Inject()(
                                 databaseService: DatabaseService,
                                 messagesRepository: MessagesRepository,
                                 queueService: QueueService
                               ) {

  import databaseService._

  def createText(channelId: ChannelId, message: String, sessionId: SessionId): Future[Message] = {
    transaction {
      for {
        m <- messagesRepository.createText(channelId, message, sessionId)
        _ <- queueService.enqueueMessage(m.id)
      } yield (m)
    }
  }

  def createMedium(channelId: ChannelId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    transaction {
      for {
        m <- messagesRepository.createMedium(channelId, mediumId, sessionId)
        _ <- queueService.enqueueMessage(m.id)
      } yield (m)
    }
  }

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      messagesRepository.delete(channelId, sessionId)
    }
  }

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int, ascending: Boolean, sessionId: SessionId): Future[Seq[Message]] = {
    transaction {
      for {
        m <- messagesRepository.find(channelId, since, offset, count, ascending, sessionId)
      } yield (m)
    }
  }

}
