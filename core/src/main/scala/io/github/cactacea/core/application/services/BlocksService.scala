package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.BlocksRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class BlocksService {

  @Inject private var db: DatabaseService = _
  @Inject private var blocksRepository: BlocksRepository = _
  @Inject private var actionService: InjectionService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- blocksRepository.create(accountId, sessionId)
        _ <- actionService.accountBlocked(accountId, sessionId)
      } yield (r)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- blocksRepository.delete(accountId, sessionId)
        _ <- actionService.accountUnBlocked(accountId, sessionId)
      } yield (r)
    }
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    blocksRepository.findAll(since, offset, count, sessionId)
  }


}
