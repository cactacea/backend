package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.infrastructure.dao.{FeedLikesDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}

@Singleton
class FeedLikesRepository @Inject()(
                                     feedLikesDAO: FeedLikesDAO,
                                     validationDAO: ValidationDAO
                                   ) {

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existFeed(feedId, sessionId)
      _ <- validationDAO.notExistFeedLike(feedId, sessionId)
      _ <- feedLikesDAO.create(feedId, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existFeed(feedId, sessionId)
      _ <- validationDAO.existFeedLike(feedId, sessionId)
      _ <- feedLikesDAO.delete(feedId, sessionId)
    } yield (Future.value(Unit))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      r <- feedLikesDAO.findAll(accountId, since, offset, count, sessionId).map(_.map({ case (f, n) => Feed(f, n)}))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedLikesDAO.findAll(since, offset, count, sessionId)
      .map(_.map({ case (f, n) => Feed(f, n)}))
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- validationDAO.existFeed(feedId, sessionId)
      r <- feedLikesDAO.findAccounts(feedId, since, offset, count, sessionId)
        .map(_.map( t => Account(t._1, t._2, t._3)))
    } yield (r)
  }

}
