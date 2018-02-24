package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.{AccountsRepository, ReportsRepository}
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import org.joda.time.DateTime

@Singleton
class AccountsService @Inject()(db: DatabaseService) {

  @Inject var accountsRepository: AccountsRepository = _
  @Inject var reportsRepository: ReportsRepository = _

  def find(sessionId: SessionId): Future[Account] = {
    accountsRepository.find(sessionId)
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsRepository.find(accountId, sessionId)
  }

  def notExist(accountName: String): Future[Boolean] = {
    accountsRepository.notExist(accountName)
  }

  def find(displayName: Option[String], since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    accountsRepository.findAll(displayName, since, offset, count, sessionId)
  }

  def update(accountId: AccountId, userName: Option[String], sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountsRepository.updateDisplayName(accountId, userName, sessionId)
    }
  }

  def update(accountName: String, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountsRepository.updateAccountName(accountName, sessionId)
    }
  }

  def update(displayName: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountsRepository.updateProfile(displayName, web, birthday, location, bio, sessionId)
    }
  }

  def update(profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountsRepository.updateProfileImage(profileImage, sessionId)
    }
  }

  def update(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      accountsRepository.updatePassword(oldPassword, newPassword, sessionId)
    }
  }


  def report(accountId: AccountId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      reportsRepository.createAccountReport(accountId, reportType, sessionId)
    }
  }

}
