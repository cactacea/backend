package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, PublishService}
import io.github.cactacea.core.domain.models.GroupInvitation
import io.github.cactacea.core.domain.repositories.GroupInvitationsRepository
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupInvitationsService {

  @Inject private var groupInvitationsRepository: GroupInvitationsRepository = _
  @Inject private var db: DatabaseService = _
  @Inject private var publishService: PublishService = _
  @Inject private var injectionService: InjectionService = _

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInvitationId]] = {
    db.transaction {
      for {
        ids <- groupInvitationsRepository.create(accountIds, groupId, sessionId)
        _ <- Future.traverseSequentially(ids) {id => publishService.enqueueGroupInvite(id)}
        _ <- injectionService.groupInvitationCreated(accountIds, groupId, sessionId)
      } yield (ids)
    }
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    db.transaction {
      for {
        id <- groupInvitationsRepository.create(accountId, groupId, sessionId)
        _ <- injectionService.groupInvitationCreated(List(accountId), groupId, sessionId)
        _ <- publishService.enqueueGroupInvite(id)
      } yield (id)
    }
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsRepository.findAll(since, offset, count, sessionId)
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- groupInvitationsRepository.accept(invitationId, sessionId)
        _ <- injectionService.groupInvitationAccepted(invitationId, sessionId)
      } yield (r)
    }
  }

  def reject(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- groupInvitationsRepository.reject(invitationId, sessionId)
        _ <- injectionService.groupInvitationRejected(invitationId, sessionId)
      } yield (r)
    }
  }

}

