package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
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
                                 actionService: InjectionService
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
           offset: Option[Int],
           count: Option[Int],
           sessionId: SessionId) : Future[List[Account]]= {

    accountsRepository.findAll(displayName, since, offset, count, sessionId)
  }

  def update(accountId: AccountId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    accountsRepository.updateDisplayName(accountId, displayName, sessionId)
  }

  def update(accountName: String, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- accountsRepository.updateAccountName(accountName, sessionId)
        _ <- actionService.accountNameUpdated(accountName, sessionId)
      } yield (r)
    }
  }

  def update(displayName: Option[String],
             web: Option[String],
             birthday: Option[Long],
             location: Option[String],
             bio: Option[String],
             sessionId: SessionId): Future[Unit] = {

    db.transaction {
      for {
        r <- accountsRepository.updateProfile(displayName, web, birthday, location, bio, sessionId)
        _ <- actionService.profileUpdated(displayName, web, birthday, location, bio, sessionId)
      } yield (r)
    }
  }

  def updateProfileImage(profileImage: MediumId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        uri <- accountsRepository.updateProfileImage(Some(profileImage), sessionId)
        r <- actionService.profileImageUpdated(uri, sessionId)
      } yield (r)
    }
  }

  def deleteProfileImage(sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- accountsRepository.updateProfileImage(None, sessionId)
        r <- actionService.profileImageUpdated(None, sessionId)
      } yield (r)
    }
  }

  def update(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- accountsRepository.updatePassword(oldPassword, newPassword, sessionId)
        _ <- actionService.passwordUpdated(sessionId)
      } yield (r)
    }
  }

  def findAccountStatus(accountId: AccountId, sessionId: SessionId): Future[AccountStatus] = {
    accountsRepository.findAccountStatus(accountId, sessionId)
  }

  def report(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- reportsRepository.createAccountReport(accountId, reportType, reportContent, sessionId)
        _ <- actionService.accountReported(accountId, reportType, reportContent, sessionId)
      } yield (r)
    }
  }

}
