package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.BlocksRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class BlocksService @Inject()(
                               db: DatabaseService,
                               blocksRepository: BlocksRepository,
                               actionService: InjectionService
                             ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- blocksRepository.create(accountId, sessionId)
        _ <- actionService.accountBlocked(accountId, sessionId)
      } yield (Unit)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- blocksRepository.delete(accountId, sessionId)
        _ <- actionService.accountUnBlocked(accountId, sessionId)
      } yield (Unit)
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    blocksRepository.find(since, offset, count, sessionId)
  }


}
