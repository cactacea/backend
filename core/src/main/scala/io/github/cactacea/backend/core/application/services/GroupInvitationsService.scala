package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.GroupInvitation
import io.github.cactacea.backend.core.domain.repositories.GroupInvitationsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers._

class GroupInvitationsService @Inject()(
                                         db: DatabaseService,
                                         groupInvitationsRepository: GroupInvitationsRepository,
                                         queueService: QueueService
                                       ) {

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInvitationId]] = {
    db.transaction {
      Future.traverseSequentially(accountIds) { id =>
        create(id, groupId, sessionId)
      }.map(_.toList)
    }
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      id <- db.transaction(groupInvitationsRepository.create(accountId, groupId, sessionId))
      _ <- queueService.enqueueGroupInvitation(id)
    } yield (id)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsRepository.find(since, offset, count, sessionId)
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(groupInvitationsRepository.accept(invitationId, sessionId))
    } yield (())
  }

  def reject(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(groupInvitationsRepository.reject(invitationId, sessionId))
    } yield (())
  }

}

