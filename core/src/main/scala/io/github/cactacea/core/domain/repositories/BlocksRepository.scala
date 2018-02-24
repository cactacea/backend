package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.factories.AccountFactory
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class BlocksRepository {

  @Inject var blocksDAO: BlocksDAO = _
  @Inject var blockersDAO: BlockersDAO = _
  @Inject var followsDAO: FollowsDAO = _
  @Inject var followersDAO: FollowersDAO = _
  @Inject var friendsDAO: FriendsDAO = _
  @Inject var mutesDAO: MutesDAO = _
  @Inject var timeLinesDAO: TimeLineDAO = _
  @Inject var friendRequestsDAO: FriendRequestsDAO = _
  @Inject var feedFavoritesDAO: FeedFavoritesDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    blocksDAO.findAll(since, offset, count, sessionId)
      .map(_.map(t => AccountFactory.create(t._1, t._2)))
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId, false)
      _ <- validationDAO.notExistBlocks(accountId, sessionId)
      _ <- blocksDAO.create(accountId, sessionId)
      _ <- blockersDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followsDAO.delete(accountId, sessionId)
      _ <- followsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendsDAO.delete(accountId, sessionId)
      _ <- friendsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
      _ <- mutesDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- timeLinesDAO.delete(accountId, sessionId)
      _ <- timeLinesDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendRequestsDAO.delete(accountId, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- feedFavoritesDAO.deleteFavorites(accountId, sessionId)
      _ <- feedFavoritesDAO.deleteFavorites(sessionId.toAccountId, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId, false)
      _ <- validationDAO.existBlocks(accountId, sessionId)
      _ <- blocksDAO.delete(accountId, sessionId)
      _ <- blockersDAO.delete(accountId, sessionId)
    } yield (Future.value(Unit))
  }

}

