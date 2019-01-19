package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.domain.repositories.FeedLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}

@Singleton
class FeedLikesService @Inject()(
                                  db: DatabaseService,
                                  feedLikesRepository: FeedLikesRepository,
                                  listenerService: ListenerService
                                ) {

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(feedLikesRepository.create(feedId, sessionId))
      _ <- listenerService.feedLiked(feedId, sessionId)
    } yield (Unit)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(feedLikesRepository.delete(feedId, sessionId))
      _ <- listenerService.feedUnLiked(feedId, sessionId)
    } yield (Unit)
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
