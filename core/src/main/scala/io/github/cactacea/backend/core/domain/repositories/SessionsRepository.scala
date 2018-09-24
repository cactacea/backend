package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, DeviceType}
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.Accounts
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class SessionsRepository {

  @Inject private var devicesDAO: DevicesDAO = _
  @Inject private var notificationSettingsDAO: PushNotificationSettingsDAO = _
  @Inject private var accountsDAO: AccountsDAO = _
  @Inject private var socialAccountsDAO: SocialAccountsDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def signUp(accountName: String, displayName: Option[String], password: String, udid: String, deviceType: DeviceType, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String], userAgent: Option[String]): Future[Account] = {
    for {
      _ <- validationDAO.notExistAccountName(accountName)
      r <- _signUp(accountName, displayName, password, udid, deviceType, web, birthday, location, bio, userAgent)
    } yield (r)
  }

  def signIn(accountName: String, password: String, udid: String, deviceType: DeviceType, userAgent: Option[String]): Future[Account] = {
    accountsDAO.find(accountName, password).flatMap(_ match {
      case Some(account) =>
        if (account.isTerminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else {
          _signIn(account, udid, deviceType, userAgent)
        }
      case None =>
        Future.exception(CactaceaException(InvalidAccountNameOrPassword))
    })
  }

  def signOut(udid: String, sessionId: SessionId): Future[Boolean] = {
    for {
      a <- accountsDAO.signOut(sessionId)
      b <- devicesDAO.delete(udid, sessionId)
    } yield (a && b)
  }


  def signUp(socialAccountType: String, accountName: String, displayName: Option[String], password: String, socialAccountId: String, accessTokenKey: String, accessTokenSecret: String, udid: String, deviceType: DeviceType, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String], userAgent: Option[String]): Future[Account] = {
    socialAccountsDAO.find(socialAccountType, socialAccountId).flatMap(_ match {
      case Some(sa) =>
        accountsDAO.find(sa.accountId.toSessionId).flatMap(_ match {
          case Some(account) =>
            if (account.isTerminated) {
              Future.exception(CactaceaException(AccountTerminated))
            } else {
              _signIn(account, udid, deviceType, userAgent)
            }
          case None =>
            Future.exception(CactaceaException(AccountNotFound))
        })
      case None =>
        _signUp(accountName, displayName, password, udid, deviceType, web, birthday, location, bio, userAgent).flatMap({ a =>
          socialAccountsDAO.update(socialAccountType, true, a.id.toSessionId).flatMap(_ match {
            case true =>
              Future.value(a)
            case false =>
              socialAccountsDAO.create(socialAccountType, socialAccountId, None, true, a.id.toSessionId).flatMap(_ => Future.value(a))
          })
        })
    })

  }

  private def _signUp(accountName: String, displayName: Option[String], password: String, udid: String, deviceType: DeviceType, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String], userAgent: Option[String]): Future[Account] = {
    (for {
      accountId <- accountsDAO.create(accountName, displayName, password, web, birthday, location, bio)
      sessionId = accountId.toSessionId
      _             <- devicesDAO.create(udid, deviceType, userAgent, sessionId)
      _             <- notificationSettingsDAO.create(true, true, true, true, true, true, sessionId)
      account       <- accountsDAO.find(sessionId)
    } yield (account)).flatMap( _ match {
      case Some(a) =>
        Future.value(Account(a))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }


  def signIn(socialAccountType: String, socialAccountId: String, accessTokenKey: String, accessTokenSecret: String, udid: String, deviceType: DeviceType, userAgent: Option[String]): Future[Account] = {
    socialAccountsDAO.find(socialAccountType, socialAccountId).flatMap(_ match {
      case Some(socialAccount) =>
        accountsDAO.find(socialAccount.accountId.toSessionId).flatMap(_ match {
          case Some(account) =>
            if (account.isTerminated) {
              Future.exception(CactaceaException(AccountTerminated))
            } else {
              _signIn(account, udid, deviceType, userAgent)
            }
          case None =>
            Future.exception(CactaceaException(AccountNotFound))
        })
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }


  private def _signIn(account: Accounts, udid: String, deviceType: DeviceType, userAgent: Option[String]): Future[Account] = {
    devicesDAO.exist(account.id.toSessionId, udid).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        devicesDAO.create(udid, deviceType, userAgent, account.id.toSessionId)
    }).map({ _ =>
      Account(account)
    })
  }



  def checkAccountStatus(sessionId: SessionId, expiresIn: Long): Future[Boolean] = {
    accountsDAO.findStatus(sessionId).flatMap( _ match {
      case Some((accountStatusType, signedOutAt)) =>
        if (accountStatusType == AccountStatusType.deleted) {
          Future.exception(CactaceaException(AccountDeleted))
        } else if (accountStatusType == AccountStatusType.terminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else if (signedOutAt.map(_ > expiresIn).getOrElse(false)) {
          Future.exception(CactaceaException(SessionTimeout))
        } else {
          Future.True
        }
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }


}
