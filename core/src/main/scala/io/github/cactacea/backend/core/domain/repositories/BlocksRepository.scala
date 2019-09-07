package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{BlocksValidator, UsersValidator}

@Singleton
class BlocksRepository @Inject()(
                                  usersValidator: UsersValidator,
                                  blocksDAO: BlocksDAO,
                                  blocksValidator: BlocksValidator,
                                  followsDAO: FollowsDAO,
                                  followersDAO: FollowersDAO,
                                  friendsDAO: FriendsDAO,
                                  friendRequestsDAO: FriendRequestsDAO,
                                  mutesDAO: MutesDAO
                                ) {

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    blocksDAO.find(userName, since, offset, count, sessionId)
  }

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId)
      _ <- blocksValidator.mustNotBlocked(userId, sessionId)
      _ <- blocksDAO.create(userId, sessionId)
      _ <- followsDAO.delete(userId, sessionId)
      _ <- followsDAO.delete(sessionId.userId, userId.sessionId)
      _ <- followersDAO.delete(userId, sessionId)
      _ <- followersDAO.delete(sessionId.userId, userId.sessionId)
      _ <- friendsDAO.delete(userId, sessionId)
      _ <- friendsDAO.delete(sessionId.userId, userId.sessionId)
      _ <- mutesDAO.delete(userId, sessionId)
      _ <- mutesDAO.delete(sessionId.userId, userId.sessionId)
      _ <- friendRequestsDAO.delete(userId, sessionId)
      _ <- friendRequestsDAO.delete(sessionId.userId, userId.sessionId)
    } yield (())
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId)
      _ <- blocksValidator.mustBlocked(userId, sessionId)
      _ <- blocksDAO.delete(userId, sessionId)
    } yield (())
  }

}

