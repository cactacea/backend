package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.BlocksRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class BlocksService @Inject()(db: DatabaseService, blocksRepository: BlocksRepository, actionService: InjectionService) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(blocksRepository.create(accountId, sessionId))
      _ <- actionService.accountBlocked(accountId, sessionId)
    } yield (r)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(blocksRepository.delete(accountId, sessionId))
      _ <- actionService.accountUnBlocked(accountId, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    blocksRepository.findAll(since, offset, count, sessionId)
  }


}
