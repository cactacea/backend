package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, BlocksValidator}

@Singleton
class BlocksRepository @Inject()(
                                  accountsValidator: AccountsValidator,
                                  blocksValidator: BlocksValidator,
                                  blocksDAO: BlocksDAO,
                                  followsDAO: FollowsDAO,
                                  followersDAO: FollowersDAO,
                                  friendsDAO: FriendsDAO,
                                  friendRequestsDAO: FriendRequestsDAO,
                                  mutesDAO: MutesDAO
                                ) {

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    blocksDAO.find(
      since,
      offset,
      count,
      sessionId
    )
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId)
      _ <- blocksValidator.notExist(accountId, sessionId)
      _ <- blocksDAO.create(accountId, sessionId)
      _ <- followsDAO.delete(accountId, sessionId)
      _ <- followsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
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
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId)
      _ <- blocksValidator.exist(accountId, sessionId)
      _ <- blocksDAO.delete(accountId, sessionId)
    } yield (())
  }

}

