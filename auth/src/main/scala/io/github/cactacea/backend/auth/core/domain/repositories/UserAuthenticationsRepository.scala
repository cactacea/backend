package io.github.cactacea.backend.auth.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.{UserAuthenticationsDAO, UsersDAO}
import io.github.cactacea.backend.core.infrastructure.validators.{UserAuthenticationsValidator, UsersValidator}
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class UserAuthenticationsRepository @Inject()(
                                           usersDAO: UsersDAO,
                                           usersValidator: UsersValidator,
                                           userAuthenticationsValidator: UserAuthenticationsValidator,
                                           userAuthenticationsDAO: UserAuthenticationsDAO,
                                  ) {

  def create(providerId: String, providerKey: String, socialProviderId: String, socialProviderKey: String): Future[Unit] = {
    for {
      u <- usersValidator.mustFind(providerId, providerKey)
      _ <- userAuthenticationsValidator.mustNotExists(u.id, socialProviderId, socialProviderKey)
      _ <- userAuthenticationsDAO.create(u.id, socialProviderId, socialProviderKey)
    } yield (())
  }

  def updateUserName(providerId: String, providerKey: String, newUserName: String): Future[Unit] = {
    for {
      u <- usersValidator.mustFind(providerId, providerKey)
      _ <- userAuthenticationsValidator.mustNotExists(CredentialsProvider.ID, newUserName)
      _ <- userAuthenticationsDAO.update(CredentialsProvider.ID, u.userName, newUserName)
      _ <- usersDAO.updateUserName(newUserName, u.id.sessionId)
    } yield (())
  }

  def notExists(providerId: String, providerKey: String): Future[Boolean] = {
    userAuthenticationsDAO.exists(providerId, providerKey)
  }

}

