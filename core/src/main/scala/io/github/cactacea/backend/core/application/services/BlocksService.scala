package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.BlocksRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

class BlocksService @Inject()(
                               db: DatabaseService,
                               blocksRepository: BlocksRepository
                             ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(blocksRepository.create(accountId, sessionId))
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(blocksRepository.delete(accountId, sessionId))
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    blocksRepository.find(since, offset, count, sessionId)
  }


}
