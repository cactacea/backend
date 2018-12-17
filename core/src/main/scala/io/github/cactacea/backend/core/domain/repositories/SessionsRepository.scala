package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, DeviceType}
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class SessionsRepository @Inject()(
                                    devicesDAO: DevicesDAO,
                                    notificationSettingsDAO: PushNotificationSettingsDAO,
                                    accountsDAO: AccountsDAO,
                                    validationDAO: ValidationDAO
                                  ) {

  def signUp(accountName: String,
             password: String,
             udid: String,
             deviceType: DeviceType,
             userAgent: Option[String]): Future[Account] = {

    val account = for {
      _ <- validationDAO.notExistAccountName(accountName)
      accountId <- accountsDAO.create(accountName, password)
      sessionId = accountId.toSessionId
      _             <- devicesDAO.create(udid, deviceType, userAgent, sessionId)
      _             <- notificationSettingsDAO.create(true, true, true, true, true, true, true, sessionId)
      account       <- accountsDAO.find(sessionId)
    } yield (account)

    account.flatMap( _ match {
      case Some(a) =>
        Future.value(Account(a))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })

  }

  def signIn(accountName: String, password: String, udid: String, deviceType: DeviceType, userAgent: Option[String]): Future[Account] = {
    accountsDAO.find(accountName, password).flatMap(_ match {
      case Some(account) =>
        if (account.isTerminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else {
          devicesDAO.exist(account.id.toSessionId, udid).flatMap(_ match {
            case true =>
              Future.True
            case false =>
              devicesDAO.create(udid, deviceType, userAgent, account.id.toSessionId)
          }).map({ _ =>
            Account(account)
          })
        }
      case None =>
        Future.exception(CactaceaException(InvalidAccountNameOrPassword))
    })
  }

  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.signOut(sessionId)
      _ <- devicesDAO.delete(udid, sessionId)
    } yield (Unit)
  }



  def checkAccountStatus(sessionId: SessionId, expiresIn: Long): Future[Unit] = {
    accountsDAO.findStatus(sessionId).flatMap( _ match {
      case Some((accountStatusType, signedOutAt)) =>
        if (accountStatusType == AccountStatusType.deleted) {
          Future.exception(CactaceaException(AccountDeleted))
        } else if (accountStatusType == AccountStatusType.terminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else if (signedOutAt.map(_ > expiresIn).getOrElse(false)) {
          Future.exception(CactaceaException(SessionTimeout))
        } else {
          Future.Unit
        }
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }


}
