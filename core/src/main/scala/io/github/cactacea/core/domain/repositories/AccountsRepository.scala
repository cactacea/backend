package io.github.cactacea.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.domain.factories.AccountFactory
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao.{AccountsDAO, MediumsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError._
import org.joda.time.DateTime

@Inject
class AccountsRepository {

  @Inject var accountsDAO: AccountsDAO = _
  @Inject var mediumsDAO: MediumsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def find(sessionId: SessionId) = {
    accountsDAO.find(sessionId).flatMap( _ match {
      case Some(t) =>
        Future.value(AccountFactory.create(t))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Account] = {
    accountsDAO.find(accountId, sessionId).flatMap( _ match {
      case Some((u, r)) =>
        Future.value(AccountFactory.create(u, r))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def notExist(accountName: String): Future[Boolean] = {
    validationDAO.notExistAccountName(accountName).flatMap(_ => Future.True)
  }

  def findAll(displayName: Option[String], since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    accountsDAO.findAll(
      displayName,
      since,
      offset,
      count,
      sessionId
    ).map(_.map(t => AccountFactory.create(t._1, t._2)))
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
      _ <- validationDAO.existAccounts(accountId)
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

  def updateProfile(displayName: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], sessionId: SessionId): Future[Unit] = {
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
        mediumsDAO.find(id).flatMap(_ match {
          case Some(m) =>
            accountsDAO.updateProfileImageUri(Some(m.uri), profileImage, sessionId).flatMap(_ match {
              case true =>
                Future.Unit
              case false =>
                Future.exception(CactaceaException(AccountNotFound))
            })
          case None =>
            Future.exception(CactaceaException(MediumNotFound))
        })
      case None =>
        accountsDAO.updateProfileImageUri(None, None, sessionId).flatMap(_ => Future.Unit)
    }
  }

}

