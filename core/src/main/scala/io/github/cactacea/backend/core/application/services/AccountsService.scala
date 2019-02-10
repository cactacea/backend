package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.{Account, AccountStatus}
import io.github.cactacea.backend.core.domain.repositories.{AccountsRepository, ReportsRepository}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}

@Singleton
class AccountsService @Inject()(
                                 db: DatabaseService,
                                 accountsRepository: AccountsRepository,
                                 reportsRepository: ReportsRepository,
                                 listenerService: ListenerService
                               ) {

  def find(sessionId: SessionId): Future[Account] = {
    accountsRepository.find(sessionId)
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsRepository.find(accountId, sessionId)
  }

  def notExist(accountName: String): Future[Boolean] = {
    accountsRepository.notExist(accountName)
  }

  def find(displayName: Option[String],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId) : Future[List[Account]]= {

    accountsRepository.find(displayName, since, offset, count, sessionId)
  }

  def update(accountId: AccountId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    accountsRepository.updateDisplayName(accountId, displayName, sessionId)
  }

  def update(accountName: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(accountsRepository.updateAccountName(accountName, sessionId))
      _ <- listenerService.accountNameUpdated(accountName, sessionId)
    } yield (Unit)
  }

  def update(displayName: String,
             web: Option[String],
             birthday: Option[Long],
             location: Option[String],
             bio: Option[String],
             sessionId: SessionId): Future[Unit] = {

    for {
      _ <- db.transaction(accountsRepository.updateProfile(displayName, web, birthday, location, bio, sessionId))
      _ <- listenerService.profileUpdated(displayName, web, birthday, location, bio, sessionId)
    } yield (Unit)

  }

  def updateProfileImage(profileImage: MediumId, sessionId: SessionId): Future[Unit] = {
    for {
      uri <- db.transaction(accountsRepository.updateProfileImage(Some(profileImage), sessionId))
      _ <- listenerService.profileImageUpdated(uri, sessionId)
    } yield (Unit)
  }

  def deleteProfileImage(sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(accountsRepository.updateProfileImage(None, sessionId))
      _ <- listenerService.profileImageUpdated(None, sessionId)
    } yield (Unit)
  }

  def update(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(accountsRepository.updatePassword(oldPassword, newPassword, sessionId))
      _ <- listenerService.passwordUpdated(sessionId)
    } yield (Unit)
  }

  def findAccountStatus(accountId: AccountId, sessionId: SessionId): Future[AccountStatus] = {
    accountsRepository.findActiveStatus(accountId, sessionId)
  }

  def report(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(reportsRepository.createAccountReport(accountId, reportType, reportContent, sessionId))
      _ <- listenerService.accountReported(accountId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

}
