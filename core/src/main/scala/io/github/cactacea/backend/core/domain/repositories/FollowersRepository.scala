package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao.FollowersDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.UsersValidator

@Singleton
class FollowersRepository @Inject()(
                                     usersValidator: UsersValidator,
                                     followersDAO: FollowersDAO
                                   ) {

  def find(userId: UserId, userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[Seq[User]]= {
    (for {
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- followersDAO.find(userId, userName, since, offset, count, sessionId)
    } yield (r))

  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[Seq[User]]= {
    followersDAO.find(sessionId.userId, userName, since, offset, count, sessionId)
  }

}
