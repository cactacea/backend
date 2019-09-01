package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.UserStatusType
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao.UsersDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class UsersValidator @Inject()(
                                   usersDAO: UsersDAO
                           ) {

  def mustNotExist(userName: String): Future[Unit] = {
    usersDAO.exists(userName).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(UserAlreadyExist))
    })
  }

  def mustNotExist(userName: String, sessionId: SessionId): Future[Unit] = {
    usersDAO.exists(userName, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(UserAlreadyExist))
    })
  }

  def mustExist(userId: UserId): Future[Unit] = {
    usersDAO.exists(userId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(UserNotFound))
    })
  }

  def mustExist(userId: UserId, sessionId: SessionId): Future[Unit] = {
    usersDAO.exists(userId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(UserNotFound))
    })
  }

  def mustNotSame(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    if (userId == by) {
      Future.exception(CactaceaException(InvalidUserIdError))
    } else {
      Future.Unit
    }
  }

  // for find a user
  def mustFind(userId: UserId, sessionId: SessionId): Future[User] = {
    usersDAO.find(userId, sessionId).flatMap(_ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(UserNotFound))
    })
  }

  // for change password
  def mustFind(sessionId: SessionId): Future[User] = {
    usersDAO.find(sessionId).flatMap( _ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }

  // for signIn
  def mustFind(sessionId: SessionId, expiresIn: Long): Future[User] = {
    usersDAO.find(sessionId).flatMap( _ match {
      case Some(a) =>
        if (a.userStatus == UserStatusType.terminated) {
          Future.exception(CactaceaException(UserTerminated))
        } else if (a.userStatus == UserStatusType.deleted) {
          Future.exception(CactaceaException(UserDeleted))
        } else if (a.signedOutAt.map(_ < expiresIn).getOrElse(false)) {
          Future.exception(CactaceaException(SessionTimeout))
        } else {
          Future.value(a)
        }
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }

  // for reset password
  def mustFind(providerId: String, providerKey: String): Future[User] = {
    usersDAO.find(providerId, providerKey).flatMap(_ match {
      case Some(a) =>
        if (a.userStatus == UserStatusType.terminated) {
          Future.exception(CactaceaException(UserTerminated))
        } else if (a.userStatus == UserStatusType.deleted) {
          Future.exception(CactaceaException(UserDeleted))
        } else {
          Future.value(a)
        }
      case None =>
        Future.exception(CactaceaException(UserNotFound))
    })
  }

}


