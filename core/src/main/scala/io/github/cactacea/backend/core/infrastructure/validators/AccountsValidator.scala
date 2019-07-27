package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.AccountsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class AccountsValidator @Inject()(
                                   accountsDAO: AccountsDAO
                           ) {

  def mustNotExist(accountName: String): Future[Unit] = {
    accountsDAO.exists(accountName).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountAlreadyExist))
    })
  }

  def mustNotExist(accountName: String, sessionId: SessionId): Future[Unit] = {
    accountsDAO.exists(accountName, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountAlreadyExist))
    })
  }

  def mustExist(accountId: AccountId): Future[Unit] = {
    accountsDAO.exists(accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def mustExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    accountsDAO.exists(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def mustNotSame(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    if (accountId == by) {
      Future.exception(CactaceaException(InvalidAccountIdError))
    } else {
      Future.Unit
    }
  }

  // for find a account
  def mustFind(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsDAO.find(accountId, sessionId).flatMap(_ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  // for change password
  def mustFind(sessionId: SessionId): Future[Account] = {
    accountsDAO.find(sessionId).flatMap( _ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }

  // for signIn
  def mustFind(sessionId: SessionId, expiresIn: Long): Future[Account] = {
    accountsDAO.find(sessionId).flatMap( _ match {
      case Some(a) =>
        if (a.accountStatus == AccountStatusType.terminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else if (a.accountStatus == AccountStatusType.deleted) {
          Future.exception(CactaceaException(AccountTerminated))
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
  def mustFind(providerId: String, providerKey: String): Future[Account] = {
    accountsDAO.find(providerId, providerKey).flatMap(_ match {
      case Some(a) =>
        if (a.accountStatus == AccountStatusType.terminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else if (a.accountStatus == AccountStatusType.deleted) {
          Future.exception(CactaceaException(AccountTerminated))
        } else {
          Future.value(a)
        }
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

}


