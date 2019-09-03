package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FollowsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyFollowed, UserNotFollowed}

@Singleton
class FollowsValidator @Inject()(followsDAO: FollowsDAO) {

  def mustFollowed(userId: UserId, sessionId: SessionId): Future[Unit] = {
    followsDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(UserNotFollowed))
    })
  }

  def mustNotFollowed(userId: UserId, sessionId: SessionId): Future[Unit] = {
    followsDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(UserAlreadyFollowed))
      case false =>
        Future.Unit
    })
  }

}
