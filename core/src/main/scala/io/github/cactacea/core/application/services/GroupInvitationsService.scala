package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, PublishService}
import io.github.cactacea.core.domain.models.GroupInvitation
import io.github.cactacea.core.domain.repositories.GroupInvitationsRepository
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupInvitationsService @Inject()(
                                         db: DatabaseService,
                                         groupInvitationsRepository: GroupInvitationsRepository,
                                         publishService: PublishService,
                                         injectionService: InjectionService) {

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInvitationId]] = {
    for {
      ids <- db.transaction(groupInvitationsRepository.create(accountIds, groupId, sessionId))
      _ <- Future.traverseSequentially(ids) {id => publishService.enqueueGroupInvite(id)}
      _ <- injectionService.groupInvitationCreated(accountIds, groupId, sessionId)
    } yield (ids)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      id <- db.transaction(groupInvitationsRepository.create(accountId, groupId, sessionId))
      _ <- publishService.enqueueGroupInvite(id)
      _ <- injectionService.groupInvitationCreated(List(accountId), groupId, sessionId)
    } yield (id)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsRepository.findAll(since, offset, count, sessionId)
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(groupInvitationsRepository.accept(invitationId, sessionId))
      _ <- injectionService.groupInvitationAccepted(invitationId, sessionId)
    } yield (r)
  }

  def reject(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(db.transaction(groupInvitationsRepository.reject(invitationId, sessionId)))
      _ <- injectionService.groupInvitationRejected(invitationId, sessionId)
    } yield (r)
  }

}

