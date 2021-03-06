package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{FriendsValidator, UsersValidator}

@Singleton
class FriendsRepository @Inject()(
                                   usersValidator: UsersValidator,
                                   friendsValidator: FriendsValidator,
                                   friendsDAO: FriendsDAO
                                 ) {

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- friendsValidator.mustFriend(userId, sessionId)
      _ <- friendsDAO.delete(userId, sessionId)
      _ <- friendsDAO.delete(sessionId.userId, userId.sessionId)
    } yield (())
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[Seq[User]]= {
    friendsDAO.find(userName, since, offset, count, sessionId)
  }

  def find(userId: UserId, userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[Seq[User]]= {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- friendsDAO.find(userId, userName, since, offset, count, sessionId)
    } yield (r)
  }


}
