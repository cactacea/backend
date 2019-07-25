package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

class AccountGroupsService @Inject()(
                                      accountGroupsRepository: AccountGroupsRepository,
                                      databaseService: DatabaseService
                                    ) {

  import databaseService._

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountGroupsRepository.delete(groupId,sessionId)
    }
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    transaction {
      accountGroupsRepository.findOrCreate(accountId, sessionId)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Group]] = {
    accountGroupsRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    accountGroupsRepository.find(since, offset, count, hidden, sessionId)
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountGroupsRepository.hide(groupId, sessionId)
    }
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountGroupsRepository.show(groupId, sessionId)
    }
  }

}
