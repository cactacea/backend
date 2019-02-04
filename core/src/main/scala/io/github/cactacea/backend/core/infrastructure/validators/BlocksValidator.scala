package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.BlocksDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyBlocked, AccountNotBlocked}

@Singleton
class BlocksValidator @Inject()(blocksDAO: BlocksDAO) {

  def exist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotBlocked))
    })
  }

  def notExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyBlocked))
      case false =>
        Future.Unit
    })
  }

}
