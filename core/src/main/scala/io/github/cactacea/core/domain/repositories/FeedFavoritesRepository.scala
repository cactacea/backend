package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.factories.{AccountFactory, FeedFactory}
import io.github.cactacea.core.domain.models.{Account, Feed}
import io.github.cactacea.core.infrastructure.dao.{FeedFavoritesDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}

@Singleton
class FeedFavoritesRepository  {

  @Inject var feedFavoritesDAO: FeedFavoritesDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existFeeds(feedId, sessionId)
      _ <- validationDAO.notExistFeedFavorites(feedId, sessionId)
      _ <- feedFavoritesDAO.create(feedId, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existFeeds(feedId, sessionId)
      _ <- validationDAO.existFeedFavorites(feedId, sessionId)
      _ <- feedFavoritesDAO.delete(feedId, sessionId)
    } yield (Future.value(Unit))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- validationDAO.existAccounts(accountId, sessionId)
      r <- feedFavoritesDAO.findAll(accountId, since, offset, count, sessionId).map(_.map(t => FeedFactory.create(t)))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedFavoritesDAO.findAll(since, offset, count, sessionId)
      .map(_.map(t => FeedFactory.create(t)))
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- validationDAO.existFeeds(feedId, sessionId)
      r <- feedFavoritesDAO.findAccounts(feedId, since, offset, count, sessionId).map(_.map(t => AccountFactory.create(t._1, t._2)))
    } yield (r)
  }

}