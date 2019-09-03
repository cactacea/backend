package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.BlocksRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}

class BlocksService @Inject()(
                               blocksRepository: BlocksRepository,
                               databaseService: DatabaseService
                             ) {

  import databaseService._

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction {
      blocksRepository.create(userId, sessionId)
    }
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction {
      blocksRepository.delete(userId, sessionId)
    }
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    blocksRepository.find(userName, since, offset, count, sessionId)
  }

}
