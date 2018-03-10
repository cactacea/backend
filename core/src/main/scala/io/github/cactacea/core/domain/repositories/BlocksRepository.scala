package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class BlocksRepository {

  @Inject private var blocksDAO: BlocksDAO = _
  @Inject private var blockersDAO: BlockersDAO = _
  @Inject private var followingDAO: FollowingDAO = _
  @Inject private var followersDAO: FollowersDAO = _
  @Inject private var friendsDAO: FriendsDAO = _
  @Inject private var mutesDAO: MutesDAO = _
  @Inject private var timeLinesDAO: TimeLineDAO = _
  @Inject private var friendRequestsDAO: FriendRequestsDAO = _
  @Inject private var feedLikesDAO: FeedLikesDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    blocksDAO.findAll(since, offset, count, sessionId)
      .map(_.map(t => Account(t._1, t._2)))
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId, false)
      _ <- validationDAO.notExistBlock(accountId, sessionId)
      _ <- blocksDAO.create(accountId, sessionId)
      _ <- blockersDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followingDAO.delete(accountId, sessionId)
      _ <- followingDAO.delete(sessionId.toAccountId, accountId.toSessionId)
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
      _ <- feedLikesDAO.deleteLikes(accountId, sessionId)
      _ <- feedLikesDAO.deleteLikes(sessionId.toAccountId, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId, false)
      _ <- validationDAO.existBlock(accountId, sessionId)
      _ <- blocksDAO.delete(accountId, sessionId)
      _ <- blockersDAO.delete(accountId, sessionId)
    } yield (Future.value(Unit))
  }

}

