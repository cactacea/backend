package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{QueueService, ListenerService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.GroupInvitation
import io.github.cactacea.backend.core.domain.repositories.GroupInvitationsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class GroupInvitationsService @Inject()(
                                         db: DatabaseService,
                                         groupInvitationsRepository: GroupInvitationsRepository,
                                         publishService: QueueService,
                                         injectionService: ListenerService
                                       ) {

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInvitationId]] = {
    db.transaction {
      Future.traverseSequentially(accountIds) { id =>
        create(id, groupId, sessionId)
      }.map(_.toList)
    }
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    db.transaction {
      for {
        id <- groupInvitationsRepository.create(accountId, groupId, sessionId)
        _ <- injectionService.groupInvitationCreated(List(accountId), groupId, sessionId)
        _ <- publishService.enqueueGroupInvitation(id)
      } yield (id)
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsRepository.find(since, offset, count, sessionId)
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- groupInvitationsRepository.accept(invitationId, sessionId)
        _ <- injectionService.groupInvitationAccepted(invitationId, sessionId)
      } yield (Unit)
    }
  }

  def reject(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- groupInvitationsRepository.reject(invitationId, sessionId)
        _ <- injectionService.groupInvitationRejected(invitationId, sessionId)
      } yield (Unit)
    }
  }

}

