package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupAccountsService @Inject()(db: DatabaseService) {

  @Inject var groupAccountsRepository: GroupAccountsRepository = _

  def find(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    groupAccountsRepository.findAll(groupId, since, offset, count, sessionId)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupAccountsRepository.create(
        groupId,
        sessionId
      )
    }
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupAccountsRepository.create(
        accountId,
        groupId,
        sessionId
      )
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupAccountsRepository.delete(
        groupId,
        sessionId
      )
    }
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupAccountsRepository.delete(
        accountId,
        groupId,
        sessionId
      )
    }
  }

}
