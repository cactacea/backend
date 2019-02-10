package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.infrastructure.dao.FeedLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, FeedLikesValidator, FeedsValidator}

@Singleton
class FeedLikesRepository @Inject()(
                                     accountsValidator: AccountsValidator,
                                     feedsValidator: FeedsValidator,
                                     feedLikesValidator: FeedLikesValidator,
                                     feedLikesDAO: FeedLikesDAO
                                   ) {

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      _ <- feedLikesValidator.notExist(feedId, sessionId)
      _ <- feedLikesDAO.create(feedId, sessionId)
    } yield (Unit)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      _ <- feedLikesValidator.exist(feedId, sessionId)
      _ <- feedLikesDAO.delete(feedId, sessionId)
    } yield (Unit)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      r <- feedLikesDAO.find(accountId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesDAO.find(since, offset, count, sessionId)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      r <- feedLikesDAO.findAccounts(feedId, since, offset, count, sessionId)
    } yield (r)
  }

}
