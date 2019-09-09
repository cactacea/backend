package io.github.cactacea.backend.auth.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.backend.auth.core.infrastructure.validators.AuthenticationsValidator
import io.github.cactacea.backend.core.infrastructure.dao.{UserAuthenticationsDAO, UsersDAO}
import io.github.cactacea.backend.core.infrastructure.validators.{UserAuthenticationsValidator, UsersValidator}
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class AuthenticationsRepository @Inject()(
                                           usersDAO: UsersDAO,
                                           usersValidator: UsersValidator,
                                           userAuthenticationsValidator: UserAuthenticationsValidator,
                                           userAuthenticationsDAO: UserAuthenticationsDAO,
                                           authenticationsDAO: AuthenticationsDAO,
                                           authenticationsValidator: AuthenticationsValidator
                                  ) {

  def find(providerId: String, providerKey: String): Future[Option[Authentication]] = {
    authenticationsDAO.find(providerId, providerKey)
  }

  def confirm(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsDAO.updateConfirm(providerId, providerKey, true)
  }


  def updateUserName(providerId: String, providerKey: String, newUserName: String): Future[Unit] = {
    for {
      u <- usersValidator.mustFind(providerId, providerKey)
      _ <- authenticationsValidator.mustNotExists(CredentialsProvider.ID, newUserName)
      _ <- userAuthenticationsValidator.mustNotExists(CredentialsProvider.ID, newUserName)
      _ <- authenticationsDAO.updateProviderKey(CredentialsProvider.ID, u.userName, newUserName)
      _ <- userAuthenticationsDAO.update(CredentialsProvider.ID, u.userName, newUserName)
      _ <- usersDAO.updateUserName(newUserName, u.id.sessionId)
    } yield (())

  }

  def mustNotExists(providerId: String, providerKey: String): Future[Unit] = {
    for {
      _ <- authenticationsValidator.mustNotExists(providerId, providerKey)
      _ <- userAuthenticationsValidator.mustNotExists(providerId, providerKey)
    } yield (())
  }

  def notExists(providerId: String, providerKey: String): Future[Boolean] = {
    for {
      r1 <- authenticationsDAO.exists(providerId, providerKey)
      r2 <- userAuthenticationsDAO.exists(providerId, providerKey)
    } yield (r1 || r2)
  }

}

