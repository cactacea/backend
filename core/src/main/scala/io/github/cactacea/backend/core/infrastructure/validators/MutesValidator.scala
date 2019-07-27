package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.MutesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyMuted, AccountNotMuted}

@Singleton
class MutesValidator @Inject()(mutesDAO: MutesDAO) {

  def mustNotMuted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    mutesDAO.own(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyMuted))
      case false =>
        Future.Unit
    })
  }

  def mustMuted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    mutesDAO.own(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotMuted))
    })
  }
}
