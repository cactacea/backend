package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, PublishService}
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.enums.NotificationType
import io.github.cactacea.core.domain.models.GroupInvitation
import io.github.cactacea.core.domain.repositories.{GroupInvitationsRepository, NotificationsRepository}
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class GroupInvitationsService {

  @Inject private var db: DatabaseService = _
  @Inject private var groupInvitationsRepository: GroupInvitationsRepository = _
  @Inject private var publishService: PublishService = _
  @Inject private var injectionService: InjectionService = _

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

