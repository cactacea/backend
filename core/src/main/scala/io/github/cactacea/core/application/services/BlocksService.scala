package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.BlocksRepository
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{SessionId, AccountId}

@Singleton
class BlocksService @Inject()(db: DatabaseService) {

  @Inject var blocksRepository: BlocksRepository = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      blocksRepository.create(accountId, sessionId)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      blocksRepository.delete(accountId, sessionId)
    }
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    blocksRepository.findAll(since, offset, count, sessionId)
  }


}
