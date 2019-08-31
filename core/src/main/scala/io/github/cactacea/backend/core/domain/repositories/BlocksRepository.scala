package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, BlocksValidator}


class BlocksRepository @Inject()(
                                  accountsValidator: AccountsValidator,
                                  blocksDAO: BlocksDAO,
                                  blocksValidator: BlocksValidator,
                                  followsDAO: FollowsDAO,
                                  followersDAO: FollowersDAO,
                                  friendsDAO: FriendsDAO,
                                  friendRequestsDAO: FriendRequestsDAO,
                                  mutesDAO: MutesDAO
                                ) {

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    blocksDAO.find(accountName, since, offset, count, sessionId)
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId)
      _ <- blocksValidator.mustNotBlocked(accountId, sessionId)
      _ <- blocksDAO.create(accountId, sessionId)
      _ <- followsDAO.delete(accountId, sessionId)
      _ <- followsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendsDAO.delete(accountId, sessionId)
      _ <- friendsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
      _ <- mutesDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendRequestsDAO.delete(accountId, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId)
      _ <- blocksValidator.mustBlocked(accountId, sessionId)
      _ <- blocksDAO.delete(accountId, sessionId)
    } yield (())
  }

}

