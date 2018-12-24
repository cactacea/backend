package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, FeedLikesDAO, FeedsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}

@Singleton
class FeedLikesRepository @Inject()(
                                     accountsDAO: AccountsDAO,
                                     feedsDAO: FeedsDAO,
                                     feedLikesDAO: FeedLikesDAO
                                   ) {

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsDAO.validateExist(feedId, sessionId)
      _ <- feedLikesDAO.validateNotExist(feedId, sessionId)
      _ <- feedLikesDAO.create(feedId, sessionId)
    } yield (Unit)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsDAO.validateExist(feedId, sessionId)
      _ <- feedLikesDAO.validateExist(feedId, sessionId)
      _ <- feedLikesDAO.delete(feedId, sessionId)
    } yield (Unit)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      r <- feedLikesDAO.find(accountId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesDAO.find(since, offset, count, sessionId)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- feedsDAO.validateExist(feedId, sessionId)
      r <- feedLikesDAO.findAccounts(feedId, since, offset, count, sessionId)
    } yield (r)
  }

}
