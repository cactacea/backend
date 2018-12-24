package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.{Account, AccountDetail, AccountStatus}
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, DevicesDAO, MediumsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class AccountsRepository @Inject()(
                                    accountsDAO: AccountsDAO,
                                    devicesDAO: DevicesDAO,
                                    mediumsDAO: MediumsDAO
                                  ) {

  def find(sessionId: SessionId): Future[AccountDetail] = {
    accountsDAO.validateFind(sessionId)
  }

  def findDetail(accountId: AccountId, sessionId: SessionId): Future[AccountDetail] = {
    accountsDAO.find(accountId, sessionId).flatMap( _ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def notExist(accountName: String): Future[Boolean] = {
    accountsDAO.exist(accountName).map(!_)
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
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      r <- devicesDAO.findActiveStatus(accountId)
    } yield (r)
  }

  def updateAccountName(accountName: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateNotExist(accountName)
      _ <- accountsDAO.updateAccountName(accountName, sessionId)
    } yield (Unit)
  }

  def updateDisplayName(accountId: AccountId, userName: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountsDAO.updateDisplayName(accountId, userName, sessionId)
    } yield (Unit)
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
          uri <- mediumsDAO.validateFind(id, sessionId).map(m => Some(m.uri))
          _ <- accountsDAO.updateProfileImageUrl(uri, profileImage, sessionId)
        } yield (uri)
      case None =>
        accountsDAO.updateProfileImageUrl(None, None, sessionId).flatMap(_ => Future.None)
    }
  }

}

