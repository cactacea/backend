package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, ReportType}
import io.github.cactacea.backend.core.domain.models.{Account, AccountStatus}
import io.github.cactacea.backend.core.infrastructure.dao.{AccountReportsDAO, AccountsDAO, DevicesDAO, PushNotificationSettingsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, MediumsValidator}


class AccountsRepository @Inject()(
                                    accountsDAO: AccountsDAO,
                                    accountsValidator: AccountsValidator,
                                    devicesDAO: DevicesDAO,
                                    accountReportsDAO: AccountReportsDAO,
                                    mediumsValidator: MediumsValidator,
                                    notificationSettingsDAO: PushNotificationSettingsDAO

                                  ) {

  def create(accountName: String): Future[Account] = {
    for {
      _ <- accountsValidator.mustNotExist(accountName)
      i <- accountsDAO.create(accountName)
      _ <- notificationSettingsDAO.create(i.toSessionId)
      a <- accountsValidator.mustFind(i.toSessionId)
    } yield (a)
  }

  def create(accountName: String, displayName: Option[String]): Future[Account] = {
    for {
      _ <- accountsValidator.mustNotExist(accountName)
      i <- accountsDAO.create(accountName, displayName.getOrElse(accountName))
      _ <- notificationSettingsDAO.create(i.toSessionId)
      a <- accountsValidator.mustFind(i.toSessionId)
    } yield (a)
  }

  def find(sessionId: SessionId): Future[Account] = {
    accountsValidator.mustFind(sessionId)
  }

  // for reset password
  def find(providerId: String, providerKey: String): Future[Account] = {
    accountsValidator.mustFind(providerId, providerKey)
  }


  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsValidator.mustFind(accountId, sessionId)
  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    accountsDAO.find(accountName, since, offset, count, sessionId)
  }

  def isRegistered(accountName: String): Future[Boolean] = {
    accountsDAO.exists(accountName)
  }

  def find(sessionId: SessionId, expiresIn: Long): Future[Account] = {
    accountsValidator.mustFind(sessionId, expiresIn)
  }

  def updateAccountStatus(accountStatus: AccountStatusType, sessionId: SessionId): Future[Unit] = {
    accountsDAO.updateAccountStatus(accountStatus, sessionId)
  }

  def updateDisplayName(accountId: AccountId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- accountsDAO.updateDisplayName(accountId, displayName, sessionId)
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

  def updateProfileImage(profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
    profileImage
      .fold(accountsDAO.updateProfileImageUrl(None, None, sessionId).map(_ => ())) { id =>
        for {
          u <- mediumsValidator.mustFind(id, sessionId).map(m => Some(m.uri))
          _ <- accountsDAO.updateProfileImageUrl(u, profileImage, sessionId)
        } yield (())
      }
  }

  def findActiveStatus(accountId: AccountId, sessionId: SessionId): Future[AccountStatus] = {
    for {
      _ <- accountsValidator.mustExist(accountId, sessionId)
      r <- devicesDAO.findActiveStatus(accountId)
    } yield (r)
  }


  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.signOut(sessionId)
      _ <- devicesDAO.delete(udid, sessionId)
    } yield (())
  }

  def report(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- accountReportsDAO.create(accountId, reportType, reportContent, sessionId)
    } yield (())
  }

}
