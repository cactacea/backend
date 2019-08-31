package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.infrastructure.dao.{FriendRequestsDAO, FriendsDAO, NotificationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, FriendRequestsValidator, FriendsValidator}


class FriendRequestsRepository @Inject()(
                                          accountsValidator: AccountsValidator,
                                          friendsValidator: FriendsValidator,
                                          friendRequestsDAO: FriendRequestsDAO,
                                          friendsDAO: FriendsDAO,
                                          friendRequestsValidator: FriendRequestsValidator,
                                          notificationsDAO: NotificationsDAO
                                        ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- friendRequestsValidator.mustNotRequested(accountId, sessionId)
      i <- friendRequestsDAO.create(accountId, sessionId)
      _ <- notificationsDAO.create(i, accountId, sessionId)
    } yield (i)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- friendRequestsValidator.mustRequested(accountId, sessionId)
      _ <- friendRequestsDAO.delete(accountId, sessionId)
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsDAO.find(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsValidator.mustFind(friendRequestId, sessionId)
      _ <- accountsValidator.mustNotSame(sessionId.toAccountId, f.toSessionId)
      _ <- accountsValidator.mustExist(sessionId.toAccountId, f.toSessionId)
      _ <- friendsValidator.mustNotFriend(sessionId.toAccountId, f.toSessionId)
      _ <- friendsDAO.create(sessionId.toAccountId, f.toSessionId)
      _ <- friendsDAO.create(f, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.toAccountId, f.toSessionId)
    } yield (())
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsValidator.mustFind(friendRequestId, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.toAccountId, f.toSessionId)
    } yield (())
  }

}
