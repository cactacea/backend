package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FriendRequestsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyRequested, FriendRequestNotFound}

@Singleton
class FriendRequestsValidator @Inject()(friendRequestsDAO: FriendRequestsDAO) {

  def mustFind(id: FriendRequestId, sessionId: SessionId): Future[UserId] = {
    friendRequestsDAO.find(id, sessionId.userId).flatMap(_ match {
      case Some(r) =>
        Future.value(r)
      case None =>
        Future.exception(CactaceaException(FriendRequestNotFound))
    })
  }


  def mustRequested(userId: UserId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.own(userId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FriendRequestNotFound))
      case true =>
        Future.Unit
    })
  }

  def mustNotRequested(userId: UserId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(UserAlreadyRequested))
      case false =>
        Future.Unit
    })
  }

}
