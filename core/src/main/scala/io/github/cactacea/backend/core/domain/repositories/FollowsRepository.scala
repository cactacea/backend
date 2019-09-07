package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao.{FollowersDAO, FollowsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{FollowsValidator, UsersValidator}

@Singleton
class FollowsRepository @Inject()(
                                   usersValidator: UsersValidator,
                                   followsDAO: FollowsDAO,
                                   followsValidator: FollowsValidator,
                                   followersDAO: FollowersDAO
                                ) {

  def find(userId: UserId, userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- followsDAO.find(userId, userName, since, offset, count, sessionId)
    } yield (r)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    followsDAO.find(userName, since, offset, count, sessionId)
  }

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- followsValidator.mustNotFollowed(userId, sessionId)
      _ <- followsDAO.create(userId, sessionId)
      _ <- followersDAO.create(sessionId.userId, userId.sessionId)
    } yield (())
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- followsValidator.mustFollowed(userId, sessionId)
      _ <- followsDAO.delete(userId, sessionId)
      _ <- followersDAO.delete(sessionId.userId, userId.sessionId)
    } yield (())
  }

}
