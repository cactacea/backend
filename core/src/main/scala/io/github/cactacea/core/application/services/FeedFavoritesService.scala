package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.{Account, Feed}
import io.github.cactacea.core.domain.repositories.FeedFavoritesRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FeedFavoritesService {

  @Inject private var db: DatabaseService = _
  @Inject private var feedFavoritesRepository: FeedFavoritesRepository = _
  @Inject private var actionService: InjectionService = _

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- feedFavoritesRepository.create(feedId, sessionId)
        _ <- actionService.feedCreated(feedId, sessionId)
      } yield (r)
    }
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- feedFavoritesRepository.delete(feedId, sessionId)
        _ <- actionService.feedDeleted(feedId, sessionId)
      } yield (r)
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

}
