package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.domain.repositories.FeedLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}

@Singleton
class FeedLikesService @Inject()(
                                  db: DatabaseService,
                                  feedLikesRepository: FeedLikesRepository,
                                  actionService: InjectionService
                                ) {

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- feedLikesRepository.create(feedId, sessionId)
        _ <- actionService.feedLiked(feedId, sessionId)
      } yield (r)
    }
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- feedLikesRepository.delete(feedId, sessionId)
        _ <- actionService.feedUnLiked(feedId, sessionId)
      } yield (r)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedLikesRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedLikesRepository.findAll(since, offset, count, sessionId)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    feedLikesRepository.findAccounts(feedId, since, offset, count, sessionId)
  }

}
