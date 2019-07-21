package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.domain.models.{Account, AccountStatus}
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, DevicesDAO, PushNotificationSettingsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Accounts
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, MediumsValidator}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._


class AccountsRepository @Inject()(
                                    accountsValidator: AccountsValidator,
                                    mediumsValidator: MediumsValidator,
                                    accountsDAO: AccountsDAO,
                                    devicesDAO: DevicesDAO,
                                    notificationSettingsDAO: PushNotificationSettingsDAO

                                  ) {

  def create(accountName: String): Future[Account] = {
    for {
      _ <- accountsValidator.notExist(accountName)
      i <- accountsDAO.create(accountName)
      _ <- notificationSettingsDAO.create(i.toSessionId)
      a <- accountsValidator.find(i.toSessionId)
    } yield (a)
  }

  def create(accountName: String, displayName: Option[String]): Future[Account] = {
    for {
      i <- accountsDAO.create(accountName, displayName.getOrElse(accountName))
      _ <- notificationSettingsDAO.create(i.toSessionId)
      a <- accountsValidator.find(i.toSessionId)
    } yield (a)
  }

  def find(sessionId: SessionId): Future[Account] = {
    accountsValidator.find(sessionId)
  }

  def find(providerId: String, providerKey: String): Future[Account] = {
    accountsValidator.find(providerId, providerKey)
  }


  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsDAO.find(accountId, sessionId).flatMap( _ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    accountsDAO.find(
      accountName,
      since,
      offset,
      count,
      sessionId
    )
  }

  def findActiveStatus(accountId: AccountId, sessionId: SessionId): Future[AccountStatus] = {
    for {
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      r <- devicesDAO.findActiveStatus(accountId)
    } yield (r)
  }

  def notExist(accountName: String): Future[Boolean] = {
    accountsDAO.exist(accountName).map(!_)
  }

  def find(sessionId: SessionId, expiresIn: Long): Future[Accounts] = {
    accountsDAO.find(sessionId).flatMap( _ match {
      case Some(a) =>
        if (a.accountStatus == AccountStatusType.deleted) {
          Future.exception(CactaceaException(AccountDeleted))
        } else if (a.accountStatus == AccountStatusType.terminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else if (a.signedOutAt.map(_ > expiresIn).getOrElse(false)) {
          Future.exception(CactaceaException(SessionTimeout))
        } else {
          Future.value(a)
        }
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }

  def updateAccountStatus(accountStatus: AccountStatusType, sessionId: SessionId): Future[Unit] = {
    accountsDAO.updateAccountStatus(accountStatus, sessionId)
  }

  def updateDisplayName(accountId: AccountId, userName: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountsDAO.updateDisplayName(accountId, userName, sessionId)
    } yield (())
  }

  def updateProfile(displayName: String,
                    web: Option[String],
                    birthday: Option[Long],
                    location: Option[String],
                    bio: Option[String],
                    sessionId: SessionId): Future[Unit] = {

    accountsDAO.updateProfile(displayName, web, birthday, location, bio, sessionId)
  }

  def updateProfileImage(profileImage: Option[MediumId], sessionId: SessionId): Future[Option[String]] = {
    profileImage match {
      case Some(id) =>
        for {
          uri <- mediumsValidator.find(id, sessionId).map(m => Some(m.uri))
          _ <- accountsDAO.updateProfileImageUrl(uri, profileImage, sessionId)
        } yield (uri)
      case None =>
        accountsDAO.updateProfileImageUrl(None, None, sessionId).flatMap(_ => Future.None)
    }
  }

  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.signOut(sessionId)
      _ <- devicesDAO.delete(udid, sessionId)
    } yield (())
  }


}

