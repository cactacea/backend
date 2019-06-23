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
                                      listenerService: ListenerService
                                    ) {

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    groupAccountsRepository.find(groupId, since, offset, count, sessionId)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(groupAccountsRepository.create(groupId, sessionId))
      _ <- listenerService.accountGroupJoined(groupId, sessionId)
    } yield (())

  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(groupAccountsRepository.create(accountId, groupId, sessionId))
      _ <- listenerService.accountGroupJoined(accountId, groupId, sessionId)
    } yield (())
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(groupAccountsRepository.delete(groupId, sessionId))
      _ <- listenerService.accountGroupLeft(groupId, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(groupAccountsRepository.delete(accountId, groupId, sessionId))
      _ <- listenerService.accountGroupLeft(accountId, groupId, sessionId)
    } yield (())
  }

}
