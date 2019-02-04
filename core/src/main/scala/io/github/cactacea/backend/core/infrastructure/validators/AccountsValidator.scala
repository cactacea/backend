package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.{AccountDetail}
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO}
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
        Future.exception(CactaceaException(AccountNameAlreadyUsed))
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

  def find(sessionId: SessionId): Future[AccountDetail] = {
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
          Future.value(AccountDetail(a))
        }
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }


  def find(accountName: String, password: String): Future[AccountDetail] = {
    accountsDAO.find(accountName, password).flatMap(_ match {
      case Some(a) =>
        if (a.isTerminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else {
          Future.value(AccountDetail(a))
        }
      case None =>
        Future.exception(CactaceaException(InvalidAccountNameOrPassword))
    })

  }




}


