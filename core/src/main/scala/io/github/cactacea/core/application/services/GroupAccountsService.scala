package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupAccountsService @Inject()(db: DatabaseService, groupAccountsRepository: GroupAccountsRepository, injectionService: InjectionService) {

  def find(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    groupAccountsRepository.findAll(groupId, since, offset, count, sessionId)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(groupAccountsRepository.create(groupId, sessionId))
      _ <- injectionService.accountGroupJoined(groupId, sessionId)
    } yield (r)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(groupAccountsRepository.create(accountId, groupId, sessionId))
      _ <- injectionService.accountGroupJoined(accountId, groupId, sessionId)
    } yield (r)
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(groupAccountsRepository.delete(groupId, sessionId))
      _ <- injectionService.accountGroupLeft(groupId, sessionId)
    } yield (r)
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(groupAccountsRepository.delete(accountId, groupId, sessionId))
      _ <- injectionService.accountGroupLeft(accountId, groupId, sessionId)
    } yield (r)
  }

}
