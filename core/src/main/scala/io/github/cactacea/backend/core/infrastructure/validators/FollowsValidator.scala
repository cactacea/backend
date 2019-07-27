package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FollowsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyFollowed, AccountNotFollowed}

@Singleton
class FollowsValidator @Inject()(followsDAO: FollowsDAO) {

  def mustFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    followsDAO.own(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFollowed))
    })
  }

  def mustNotFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    followsDAO.own(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFollowed))
      case false =>
        Future.Unit
    })
  }

}
