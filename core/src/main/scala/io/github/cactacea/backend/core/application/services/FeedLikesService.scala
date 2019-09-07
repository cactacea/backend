package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.{Feed, User}
import io.github.cactacea.backend.core.domain.repositories.FeedLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, SessionId, UserId}

@Singleton
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

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesRepository.find(userId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesRepository.find(since, offset, count, sessionId)
  }

  def findUsers(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[User]] = {
    feedLikesRepository.findUsers(feedId, since, offset, count, sessionId)
  }

}
