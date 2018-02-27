package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.{Account, Feed}
import io.github.cactacea.core.domain.repositories.FeedFavoritesRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
import io.github.cactacea.core.infrastructure.services.{DatabaseService, QueueService}

@Singleton
class FeedFavoritesService @Inject()(db: DatabaseService) {

  @Inject var feedFavoritesRepository: FeedFavoritesRepository = _
  @Inject var queueService: QueueService = _

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        a <- feedFavoritesRepository.create(feedId, sessionId)
        _ <- queueService.enqueueNoticeFeedFavorite(feedId)
      } yield (a)
    }
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      feedFavoritesRepository.delete(feedId, sessionId)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedFavoritesRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedFavoritesRepository.findAll(since, offset, count, sessionId)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    feedFavoritesRepository.findAccounts(feedId, since, offset, count, sessionId)
  }

  def notice(feedId: FeedId): Future[Unit] = {
    Future.Unit
  }

}
