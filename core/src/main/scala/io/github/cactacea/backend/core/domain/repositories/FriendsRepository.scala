package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{UsersValidator, FriendsValidator}


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

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    friendsDAO.find(userName, since, offset, count, sessionId)
  }

  def find(userId: UserId, userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- friendsDAO.find(userId, userName, since, offset, count, sessionId)
    } yield (r)
  }


}
