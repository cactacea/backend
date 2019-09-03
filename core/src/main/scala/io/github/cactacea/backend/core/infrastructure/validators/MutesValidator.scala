package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.MutesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyMuted, UserNotMuted}

@Singleton
class MutesValidator @Inject()(mutesDAO: MutesDAO) {

  def mustNotMuted(userId: UserId, sessionId: SessionId): Future[Unit] = {
    mutesDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(UserAlreadyMuted))
      case false =>
        Future.Unit
    })
  }

  def mustMuted(userId: UserId, sessionId: SessionId): Future[Unit] = {
    mutesDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(UserNotMuted))
    })
  }
}
