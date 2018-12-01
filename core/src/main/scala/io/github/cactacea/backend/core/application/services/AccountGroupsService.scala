package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

@Singleton
class AccountGroupsService @Inject()(
                                      db: DatabaseService,
                                      accountGroupsRepository: AccountGroupsRepository,
                                      actionService: InjectionService
                                    ) {

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(accountGroupsRepository.delete(groupId,sessionId))
      _ <- actionService.groupDeleted(groupId, sessionId)
    } yield (r)
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    db.transaction {
      accountGroupsRepository.findOrCreate(
        accountId,
        sessionId
      )
    }
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Group]] = {
    accountGroupsRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def findAll(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    accountGroupsRepository.findAll(since, offset, count, hidden, sessionId)
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountGroupsRepository.hide(groupId, sessionId)
    }
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountGroupsRepository.show(groupId, sessionId)
    }
  }

}
