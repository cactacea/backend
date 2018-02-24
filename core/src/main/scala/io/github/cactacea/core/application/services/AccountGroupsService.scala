package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.domain.repositories._
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, SessionId, AccountId}

@Singleton
class AccountGroupsService @Inject()(db: DatabaseService) {

  @Inject var accountGroupsRepository: AccountGroupsRepository = _

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountGroupsRepository.delete(
        groupId,
        sessionId
      )
    }
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    db.transaction {
      accountGroupsRepository.findOrCreate(
        accountId,
        sessionId
      )
    }
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Group]] = {
    accountGroupsRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
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
