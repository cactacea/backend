package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.AccountsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class AccountsValidator @Inject()(
                                   db: DatabaseService,
                                   accountsDAO: AccountsDAO
                           ) {

  import db._

  def notExist(accountName: String): Future[Unit] = {
    accountsDAO.exist(accountName).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountAlreadyExist))
    })
  }

  def notExist(accountName: String, sessionId: SessionId): Future[Unit] = {
    accountsDAO.exist(accountName, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountAlreadyExist))
    })
  }

  def exist(accountId: AccountId): Future[Unit] = {
    accountsDAO.exist(accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    accountsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def checkSessionId(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    if (accountId == by) {
      Future.exception(CactaceaException(CanNotSpecifyMyself))
    } else {
      Future.Unit
    }
  }

  def find(sessionId: SessionId): Future[Account] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
    }
    run(q).map(_.headOption).flatMap( _ match {
      case Some(a) =>
        if (a.isTerminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else {
          Future.value(Account(a))
        }
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def find(sessionId: SessionId, expiresIn: Long): Future[Account] = {
    accountsDAO.find(sessionId).flatMap( _ match {
      case Some(a) =>
        if (a.accountStatus == AccountStatusType.deleted) {
          Future.exception(CactaceaException(AccountDeleted))
        } else if (a.accountStatus == AccountStatusType.terminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else if (a.signedOutAt.map(_ > expiresIn).getOrElse(false)) {
          Future.exception(CactaceaException(SessionTimeout))
        } else {
          Future.value(Account(a))
        }
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }

  def find(providerId: String, providerKey: String): Future[Account] = {
    accountsDAO.find(providerId, providerKey).flatMap(_ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def find(accountName: String): Future[Account] = {
    accountsDAO.find(accountName).flatMap(_ match {
      case Some(a) =>
        if (a.isTerminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else {
          Future.value(Account(a))
        }
      case None =>
        Future.exception(CactaceaException(InvalidAccountNameOrPassword))
    })
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsDAO.find(accountId, sessionId).flatMap( _ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

}


