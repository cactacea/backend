package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

class GroupAccountsService @Inject()(
                                      databaseService: DatabaseService,
                                      groupAccountsRepository: GroupAccountsRepository
                                    ) {

  import databaseService._

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    groupAccountsRepository.find(groupId, since, offset, count, sessionId)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction{
      groupAccountsRepository.create(groupId, sessionId)
    }
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction{
      groupAccountsRepository.create(accountId, groupId, sessionId)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction{
      groupAccountsRepository.delete(groupId, sessionId)
    }
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    transaction{
      groupAccountsRepository.delete(accountId, groupId, sessionId)
    }
  }

}
