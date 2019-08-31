package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.infrastructure.dao.FeedLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, FeedLikesValidator, FeedsValidator}


class FeedLikesRepository @Inject()(
                                     accountsValidator: AccountsValidator,
                                     feedsValidator: FeedsValidator,
                                     feedLikesValidator: FeedLikesValidator,
                                     feedLikesDAO: FeedLikesDAO
                                   ) {

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      _ <- feedLikesValidator.mustNotLiked(feedId, sessionId)
      _ <- feedLikesDAO.create(feedId, sessionId)
    } yield (())
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      _ <- feedLikesValidator.mustLiked(feedId, sessionId)
      _ <- feedLikesDAO.delete(feedId, sessionId)
    } yield (())
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- accountsValidator.mustExist(accountId, sessionId)
      r <- feedLikesDAO.find(accountId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesDAO.find(sessionId.toAccountId, since, offset, count, sessionId)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      r <- feedLikesDAO.findAccounts(feedId, since, offset, count, sessionId)
    } yield (r)
  }

}
