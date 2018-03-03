package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.PublishService
import io.github.cactacea.core.domain.models.GroupInvite
import io.github.cactacea.core.domain.repositories.GroupInvitesRepository
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupInvitesService @Inject()(db: DatabaseService) {

  @Inject var groupInvitesRepository: GroupInvitesRepository = _
  @Inject var publishService: PublishService = _

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInviteId]] = {
    for {
      ids <- db.transaction(groupInvitesRepository.create(accountIds, groupId, sessionId))
      _ <- Future.traverseSequentially(ids) {id => publishService.enqueueGroupInvite(id)}
    } yield (ids)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInviteId] = {
    for {
      id <- db.transaction(groupInvitesRepository.create(accountId, groupId, sessionId))
      _ <- publishService.enqueueGroupInvite(id)
    } yield (id)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[GroupInvite]] = {
    groupInvitesRepository.findAll(since, offset, count, sessionId)
  }

  def accept(inviteId: GroupInviteId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupInvitesRepository.accept(inviteId, sessionId)
    }
  }

  def reject(inviteId: GroupInviteId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupInvitesRepository.reject(inviteId, sessionId)
    }
  }

}

