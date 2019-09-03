package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao.MutesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{UsersValidator, MutesValidator}


class MutesRepository @Inject()(
                                 usersValidator: UsersValidator,
                                 mutesDAO: MutesDAO,
                                 mutesValidator: MutesValidator
                               ) {

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- mutesValidator.mustNotMuted(userId, sessionId)
      _ <- mutesDAO.create(userId, sessionId)
    } yield (())
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- mutesValidator.mustMuted(userId, sessionId)
      _ <- mutesDAO.delete(userId, sessionId)
    } yield (())
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    mutesDAO.find(userName, since, offset, count, sessionId)
  }

}
