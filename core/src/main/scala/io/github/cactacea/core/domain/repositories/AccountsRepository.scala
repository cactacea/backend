package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao.{AccountsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError._

@Singleton
class AccountsRepository {

  @Inject private var accountsDAO: AccountsDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def find(sessionId: SessionId) = {
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
    accountsDAO.exist(accountName)
  }

  def findAll(displayName: Option[String], since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    accountsDAO.findAll(
      displayName,
      since,
      offset,
      count,
      sessionId
    ).map(_.map(t => Account(t._1, t._2)))
  }

  def updateAccountName(accountName: String, sessionId: SessionId): Future[Unit] = {
    validationDAO.notExistAccountName(accountName).flatMap(_ =>
      accountsDAO.updateAccountName(accountName, sessionId).flatMap(_ match {
        case true =>
          Future.Unit
        case false =>
          Future.exception(CactaceaException(AccountNotFound))
      })
    )
  }

  def updateDisplayName(accountId: AccountId, userName: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existAccount(accountId)
      _ <- accountsDAO.updateDisplayName(accountId, userName, sessionId)
    } yield (Future.value(Unit))
  }

  def updatePassword(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    accountsDAO.updatePassword(oldPassword, newPassword, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def updateProfile(displayName: String, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String], sessionId: SessionId): Future[Unit] = {
    accountsDAO.updateProfile(displayName, web, birthday, location, bio, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def updateProfileImage(profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
    profileImage match {
      case Some(id) =>
        for {
          m <- validationDAO.findMedium(id, sessionId)
          _ <- accountsDAO.updateProfileImageUri(Some(m.uri), profileImage, sessionId)
        } yield (Future.value(Unit))
      case None =>
        accountsDAO.updateProfileImageUri(None, None, sessionId).flatMap(_ => Future.Unit)
    }
  }

}

