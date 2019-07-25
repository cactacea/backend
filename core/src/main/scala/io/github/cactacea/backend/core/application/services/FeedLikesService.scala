package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.domain.repositories.FeedLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}

class FeedLikesService @Inject()(
                                  databaseService: DatabaseService,
                                  feedLikesRepository: FeedLikesRepository
                                ) {

  import databaseService._

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    transaction {
      feedLikesRepository.create(feedId, sessionId)
    }
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    transaction {
      feedLikesRepository.delete(feedId, sessionId)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesRepository.find(since, offset, count, sessionId)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    feedLikesRepository.findAccounts(feedId, since, offset, count, sessionId)
  }

}
