package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.BlocksDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyBlocked, UserNotBlocked}

@Singleton
class BlocksValidator @Inject()(blocksDAO: BlocksDAO) {

  def mustBlocked(userId: UserId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(UserNotBlocked))
    })
  }

  def mustNotBlocked(userId: UserId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(UserAlreadyBlocked))
      case false =>
        Future.Unit
    })
  }

}
