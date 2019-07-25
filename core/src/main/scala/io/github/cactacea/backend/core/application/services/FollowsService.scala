package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.FollowsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

class FollowsService @Inject()(
                                databaseService: DatabaseService,
                                followsRepository: FollowsRepository
                              ) {

  import databaseService._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    transaction {
      followsRepository.create(accountId, sessionId)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    transaction {
      followsRepository.delete(accountId, sessionId)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.find(since, offset, count, sessionId)
  }

}
