package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.{Account, AccountStatus}
import io.github.cactacea.backend.core.domain.repositories.{AccountsRepository, AuthenticationsRepository}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}

class AccountsService @Inject()(
                                 accountsRepository: AccountsRepository,
                                 authenticationsRepository: AuthenticationsRepository,
                                 databaseService: DatabaseService
                               ) {

  import databaseService._

  def find(sessionId: SessionId): Future[Account] = {
    accountsRepository.find(sessionId)
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsRepository.find(accountId, sessionId)
  }

  def isRegistered(accountName: String): Future[Boolean] = {
    accountsRepository.isRegistered(accountName)
  }

  def find(displayName: Option[String],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId) : Future[List[Account]]= {

    accountsRepository.find(displayName, since, offset, count, sessionId)
  }

  def updateDisplayName(accountId: AccountId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      accountsRepository.updateDisplayName(accountId, displayName, sessionId)
    }
  }

  def updateProfile(displayName: String,
                    web: Option[String],
                    birthday: Option[Long],
                    location: Option[String],
                    bio: Option[String],
                    sessionId: SessionId): Future[Unit] = {

    transaction {
      accountsRepository.updateProfile(displayName, web, birthday, location, bio, sessionId)
    }
  }

  def updateProfileImage(profileImage: MediumId, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountsRepository.updateProfileImage(Some(profileImage), sessionId)
    }
  }

  def changeAccountName(providerId: String, providerKey: String, sessionId: SessionId): Future[Unit] = {
    transaction {
      authenticationsRepository.updateAccountName(providerId, providerKey, sessionId)
    }
  }

  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountsRepository.signOut(udid, sessionId)
    }
  }

  def deleteProfileImage(sessionId: SessionId): Future[Unit] = {
    transaction {
      accountsRepository.updateProfileImage(None, sessionId)
    }
  }

  def findAccountStatus(accountId: AccountId, sessionId: SessionId): Future[AccountStatus] = {
    accountsRepository.findActiveStatus(accountId, sessionId)
  }

  def report(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      accountsRepository.report(accountId, reportType, reportContent, sessionId)
    }
  }

}
