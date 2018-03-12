package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.{Account, Feed}
import io.github.cactacea.core.domain.repositories.FeedLikesRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}

@Singleton
class FeedLikesService {

  @Inject private var db: DatabaseService = _
  @Inject private var feedLikesRepository: FeedLikesRepository = _
  @Inject private var actionService: InjectionService = _

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- feedLikesRepository.create(feedId, sessionId)
        _ <- actionService.feedCreated(feedId, sessionId)
      } yield (r)
    }
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- feedLikesRepository.delete(feedId, sessionId)
        _ <- actionService.feedDeleted(feedId, sessionId)
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