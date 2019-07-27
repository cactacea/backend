package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.BlocksRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

class BlocksService @Inject()(
                               blocksRepository: BlocksRepository,
                               databaseService: DatabaseService
                             ) {

  import databaseService._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    transaction {
      blocksRepository.create(accountId, sessionId)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    transaction {
      blocksRepository.delete(accountId, sessionId)
    }
  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    blocksRepository.find(accountName, since, offset, count, sessionId)
  }

}
