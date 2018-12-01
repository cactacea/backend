package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ActiveStatus
import io.github.cactacea.backend.core.domain.models.{Account, AccountStatus}
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, DevicesDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class AccountsRepository @Inject()(
                                    accountsDAO: AccountsDAO,
                                    devicesDAO: DevicesDAO,
                                    validationDAO: ValidationDAO
                                  ) {

  def find(sessionId: SessionId): Future[Account] = {
    validationDAO.findAccount(sessionId.toAccountId).map(Account(_))
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsDAO.find(accountId, sessionId).flatMap( _ match {
      case Some((u, r)) =>
        Future.value(Account(u, r))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def notExist(accountName: String): Future[Boolean] = {
    accountsDAO.exist(accountName).map(!_)
  }

  def findAll(displayName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    accountsDAO.findAll(
      displayName,
      since,
      offset,
      count,
      sessionId
    ).map(_.map({ case (a, r) => Account(a, r)}))
  }

  def findAccountStatus(accountId: AccountId, sessionId: SessionId): Future[AccountStatus] = {
    (for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      r <- devicesDAO.findActiveStatus(accountId)
    } yield (r)).map(_ match {
      case Some(s) =>
        AccountStatus(accountId, s)
      case None =>
        AccountStatus(accountId, ActiveStatus.inactive)
    })
  }

  def updateAccountName(accountName: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notExistAccountName(accountName)
      r <- accountsDAO.updateAccountName(accountName, sessionId)
    } yield (r)
  }

  def updateDisplayName(accountId: AccountId, userName: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountsDAO.updateDisplayName(accountId, userName, sessionId)
    } yield (Future.value(Unit))
  }

  def updatePassword(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    accountsDAO.updatePassword(oldPassword, newPassword, sessionId)
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
          uri <- validationDAO.findMedium(id, sessionId).map(m => Some(m.uri))
          _ <- accountsDAO.updateProfileImageUri(uri, profileImage, sessionId)
        } yield (uri)
      case None =>
        accountsDAO.updateProfileImageUri(None, None, sessionId).flatMap(_ => Future.None)
    }
  }

}

