package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FriendRequestsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyRequested, FriendRequestNotFound}

@Singleton
class FriendRequestsValidator @Inject()(friendRequestsDAO: FriendRequestsDAO) {

  def exist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FriendRequestNotFound))
      case true =>
        Future.Unit
    })
  }

  def nnotExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyRequested))
      case false =>
        Future.Unit
    })
  }

}
