package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class BlocksRepository @Inject()(
                                  blocksDAO: BlocksDAO,
                                  followingsDAO: FollowingsDAO,
                                  followersDAO: FollowersDAO,
                                  friendsDAO: FriendsDAO,
                                  mutesDAO: MutesDAO,
                                  friendRequestsDAO: FriendRequestsDAO,
                                  validationDAO: ValidationDAO
                                ) {

  def findAll(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    blocksDAO.findAll(
      since,
      offset,
      count,
      sessionId
    )
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId)
      _ <- validationDAO.notExistBlock(accountId, sessionId)
      _ <- blocksDAO.create(accountId, sessionId)
      _ <- followingsDAO.delete(accountId, sessionId)
      _ <- followingsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendsDAO.delete(accountId, sessionId)
      _ <- friendsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
      _ <- mutesDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendRequestsDAO.delete(accountId, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId)
      _ <- validationDAO.existBlock(accountId, sessionId)
      _ <- blocksDAO.delete(accountId, sessionId)
    } yield (Future.value(Unit))
  }

}

