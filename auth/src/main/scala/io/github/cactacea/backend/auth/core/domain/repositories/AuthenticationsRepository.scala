package io.github.cactacea.backend.auth.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.backend.auth.core.infrastructure.validators.AuthenticationsValidator
import io.github.cactacea.backend.core.infrastructure.dao.UsersDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.infrastructure.validators.UsersValidator
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.services.IdentityService
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

class AuthenticationsRepository @Inject()(authenticationsDAO: AuthenticationsDAO,
                                          authenticationsValidator: AuthenticationsValidator,
                                          usersDAO: UsersDAO,
                                          usersValidator: UsersValidator) extends IdentityService[Authentication] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[Authentication]] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey)
      .flatMap(_ match {
        case Some(a) =>
          Future.value(Option(a))
        case None =>
          Future.None
      })
  }

  def find(loginInfo: LoginInfo): Future[Option[Authentication]] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey)
  }

  def confirm(loginInfo: LoginInfo): Future[Unit] = {
    authenticationsDAO.updateConfirm(loginInfo.providerId, loginInfo.providerKey, true)
  }

  def exists(providerId: String, providerKey: String): Future[Boolean] = {
    authenticationsDAO.exists(providerId, providerKey)
  }

  def updateUserName(providerId: String, providerKey: String, newUserName: String): Future[Unit] = {
    updateProviderKey(providerId, providerKey, newUserName).flatMap(_.userId match {
      case Some(userId) =>
        usersDAO.updateUserName(newUserName, userId.sessionId)
      case None =>
        Future.Unit
    })
  }

  def link(providerId: String, providerKey: String, userId: UserId): Future[Unit] = {
    authenticationsDAO.updateUserId(providerId, providerKey, userId)
  }

  private def updateProviderKey(providerId: String, providerKey: String, newUserName: String): Future[Authentication] = {
    providerId match {
      case CredentialsProvider.ID =>
        for {
          a <- authenticationsValidator.mustFind(providerId: String, providerKey)
          _ <- authenticationsValidator.mustNotExists(CredentialsProvider.ID, newUserName)
          _ <- authenticationsDAO.updateProviderKey(providerId, providerKey, newUserName)
        } yield (a)
      case _ =>
        for {
          a <- authenticationsValidator.mustFind(providerId: String, providerKey)
          _ <- authenticationsDAO.updateProviderKey(CredentialsProvider.ID, a.userId, newUserName)
        } yield (a)
    }
  }

}


//          _ <- authenticationsDAO.find(a.userName, userId)
//      u <- authenticationsValidator.mustFind(providerId, providerKey)
//      _ <- usersDAO.updateUserName(newUserName, u.id.sessionId)
//    for {
//      a <- authenticationsValidator.mustFind(providerId, providerKey)
//      _ <- authenticationsDAO.find(a.userName, userId)
//      //      u <- authenticationsValidator.mustFind(providerId, providerKey)
////      _ <- usersDAO.updateUserName(newUserName, u.id.sessionId)
//    } yield (())
