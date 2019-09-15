package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.infrastructure.dao.{FriendRequestsDAO, FriendsDAO, FeedsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{FriendRequestId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{FriendRequestsValidator, FriendsValidator, UsersValidator}

@Singleton
class FriendRequestsRepository @Inject()(
                                          usersValidator: UsersValidator,
                                          friendsValidator: FriendsValidator,
                                          friendRequestsDAO: FriendRequestsDAO,
                                          friendsDAO: FriendsDAO,
                                          friendRequestsValidator: FriendRequestsValidator,
                                          notificationsDAO: FeedsDAO
                                        ) {

  def create(userId: UserId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- friendRequestsValidator.mustNotRequested(userId, sessionId)
      i <- friendRequestsDAO.create(userId, sessionId)
      _ <- notificationsDAO.create(i, userId, sessionId)
    } yield (i)
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- friendRequestsValidator.mustRequested(userId, sessionId)
      _ <- friendRequestsDAO.delete(userId, sessionId)
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[Seq[FriendRequest]] = {
    friendRequestsDAO.find(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsValidator.mustFind(friendRequestId, sessionId)
      _ <- usersValidator.mustNotSame(sessionId.userId, f.sessionId)
      _ <- usersValidator.mustExist(sessionId.userId, f.sessionId)
      _ <- friendsValidator.mustNotFriend(sessionId.userId, f.sessionId)
      _ <- friendsDAO.create(sessionId.userId, f.sessionId)
      _ <- friendsDAO.create(f, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.userId, f.sessionId)
    } yield (())
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsValidator.mustFind(friendRequestId, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.userId, f.sessionId)
    } yield (())
  }

}
