package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.{AccountsRepository, ReportsRepository}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService
import org.joda.time.DateTime

@Singleton
class AccountsService @Inject()(db: DatabaseService, accountsRepository: AccountsRepository, reportsRepository: ReportsRepository, actionService: InjectionService) {

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

  def update(accountId: AccountId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(accountsRepository.updateDisplayName(accountId, displayName, sessionId))
      _ <- actionService.displayNameUpdated(sessionId)
    } yield (r)
  }

  def update(accountName: String, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(accountsRepository.updateAccountName(accountName, sessionId))
      _ <- actionService.accountNameUpdated(sessionId)
    } yield (r)
  }

  def update(displayName: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(accountsRepository.updateProfile(displayName, web, birthday, location, bio, sessionId))
      _ <- actionService.profileUpdated(sessionId)
    } yield (r)

  }

  def update(profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(accountsRepository.updateProfileImage(profileImage, sessionId))
      _ <- actionService.profileImageUpdated(sessionId)
    } yield (r)
  }

  def update(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(accountsRepository.updatePassword(oldPassword, newPassword, sessionId))
      _ <- actionService.passwordUpdated(sessionId)
    } yield (r)
  }


  def report(accountId: AccountId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(reportsRepository.createAccountReport(accountId, reportType, sessionId))
      _ <- actionService.accountReported(accountId, reportType, sessionId)
    } yield (r)

  }

}
