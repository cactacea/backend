package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

@Singleton
class GroupAccountsService @Inject()(
                                      db: DatabaseService,
                                      groupAccountsRepository: GroupAccountsRepository,
                                      injectionService: ListenerService
                                    ) {

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    groupAccountsRepository.find(groupId, since, offset, count, sessionId)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- groupAccountsRepository.create(groupId, sessionId)
        _ <- injectionService.accountGroupJoined(groupId, sessionId)
      } yield (Unit)
    }
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- groupAccountsRepository.create(accountId, groupId, sessionId)
        _ <- injectionService.accountGroupJoined(accountId, groupId, sessionId)
      } yield (Unit)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- groupAccountsRepository.delete(groupId, sessionId)
        _ <- injectionService.accountGroupLeft(groupId, sessionId)
      } yield (Unit)
    }
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- groupAccountsRepository.delete(accountId, groupId, sessionId)
        _ <- injectionService.accountGroupLeft(accountId, groupId, sessionId)
      } yield (Unit)
    }
  }

}
