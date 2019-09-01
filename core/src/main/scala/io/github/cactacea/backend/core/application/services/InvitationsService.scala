package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Invitation
import io.github.cactacea.backend.core.domain.repositories.InvitationsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers._

class InvitationsService @Inject()(
                                    databaseService: DatabaseService,
                                    invitationsRepository: InvitationsRepository,
                                    queueService: QueueService
                                       ) {

  import databaseService._

  def create(userIds: List[UserId], channelId: ChannelId, sessionId: SessionId): Future[List[InvitationId]] = {
    transaction {
      Future.traverseSequentially(userIds) { userId =>
        for {
          i <- invitationsRepository.create(userId, channelId, sessionId)
          _ <- queueService.enqueueInvitation(i)
        } yield (i)
      }.map(_.toList)
    }
  }

  def create(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[InvitationId] = {
    transaction {
      for {
        i <- invitationsRepository.create(userId, channelId, sessionId)
        _ <- queueService.enqueueInvitation(i)
      } yield (i)
    }
  }

  def delete(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      invitationsRepository.delete(userId, channelId, sessionId)
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Invitation]] = {
    invitationsRepository.find(since, offset, count, sessionId)
  }

  def accept(invitationId: InvitationId, sessionId: SessionId): Future[Unit] = {
    transaction {
      invitationsRepository.accept(invitationId, sessionId)
    }
  }

  def reject(invitationId: InvitationId, sessionId: SessionId): Future[Unit] = {
    transaction {
      invitationsRepository.reject(invitationId, sessionId)
    }
  }

}

