package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

@Singleton
class GroupAccountsService @Inject()(
                                      db: DatabaseService,
                                      groupAccountsRepository: GroupAccountsRepository,
                                      injectionService: InjectionService
                                    ) {

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    groupAccountsRepository.findAll(groupId, since, offset, count, sessionId)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- groupAccountsRepository.create(groupId, sessionId)
        _ <- injectionService.accountGroupJoined(groupId, sessionId)
      } yield (r)
    }
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- groupAccountsRepository.create(accountId, groupId, sessionId)
        _ <- injectionService.accountGroupJoined(accountId, groupId, sessionId)
      } yield (r)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- groupAccountsRepository.delete(groupId, sessionId)
        _ <- injectionService.accountGroupLeft(groupId, sessionId)
      } yield (r)
    }
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- groupAccountsRepository.delete(accountId, groupId, sessionId)
        _ <- injectionService.accountGroupLeft(accountId, groupId, sessionId)
      } yield (r)
    }
  }

}
