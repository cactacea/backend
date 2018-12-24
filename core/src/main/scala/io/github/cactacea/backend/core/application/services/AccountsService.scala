package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.{Account, AccountDetail, AccountStatus}
import io.github.cactacea.backend.core.domain.repositories.{AccountsRepository, ReportsRepository}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}

@Singleton
class AccountsService @Inject()(
                                 db: DatabaseService,
                                 accountsRepository: AccountsRepository,
                                 reportsRepository: ReportsRepository,
                                 actionService: InjectionService
                               ) {

  def find(sessionId: SessionId): Future[AccountDetail] = {
    accountsRepository.find(sessionId)
  }

  def findDetail(accountId: AccountId, sessionId: SessionId): Future[AccountDetail] = {
    accountsRepository.findDetail(accountId, sessionId)
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
    db.transaction {
      for {
        _ <- accountsRepository.updateAccountName(accountName, sessionId)
        _ <- actionService.accountNameUpdated(accountName, sessionId)
      } yield (Unit)
    }
  }

  def update(displayName: String,
             web: Option[String],
             birthday: Option[Long],
             location: Option[String],
             bio: Option[String],
             sessionId: SessionId): Future[Unit] = {

    db.transaction {
      for {
        _ <- accountsRepository.updateProfile(displayName, web, birthday, location, bio, sessionId)
        _ <- actionService.profileUpdated(displayName, web, birthday, location, bio, sessionId)
      } yield (Unit)
    }
  }

  def updateProfileImage(profileImage: MediumId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        uri <- accountsRepository.updateProfileImage(Some(profileImage), sessionId)
        _ <- actionService.profileImageUpdated(uri, sessionId)
      } yield (Unit)
    }
  }

  def deleteProfileImage(sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- accountsRepository.updateProfileImage(None, sessionId)
        _ <- actionService.profileImageUpdated(None, sessionId)
      } yield (Unit)
    }
  }

  def update(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- accountsRepository.updatePassword(oldPassword, newPassword, sessionId)
        _ <- actionService.passwordUpdated(sessionId)
      } yield (Unit)
    }
  }

  def findAccountStatus(accountId: AccountId, sessionId: SessionId): Future[AccountStatus] = {
    accountsRepository.findActiveStatus(accountId, sessionId)
  }

  def report(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- reportsRepository.createAccountReport(accountId, reportType, reportContent, sessionId)
        _ <- actionService.accountReported(accountId, reportType, reportContent, sessionId)
      } yield (Unit)
    }
  }

}
