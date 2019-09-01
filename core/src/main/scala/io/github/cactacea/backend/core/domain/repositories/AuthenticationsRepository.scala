package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.{UsersDAO, AuthenticationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Authentications
import io.github.cactacea.backend.core.infrastructure.validators.{UsersValidator, AuthenticationsValidator}


class AuthenticationsRepository @Inject()(
                                           usersDAO: UsersDAO,
                                           usersValidator: UsersValidator,
                                           authenticationsDAO: AuthenticationsDAO,
                                           authenticationsValidator: AuthenticationsValidator
                                  ) {

  def find(providerId: String, providerKey: String): Future[Option[Authentications]] = {
    authenticationsDAO.find(providerId, providerKey)
  }

  def updateUserName(providerId: String, providerKey: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotExist(providerKey, sessionId)
      _ <- usersDAO.updateUserName(providerKey, sessionId)
      _ <- authenticationsDAO.updateProviderKey(providerId, providerKey, sessionId)
    } yield (())
  }

  def link(providerId: String, providerKey: String, sessionId: SessionId): Future[Unit] = {
    authenticationsDAO.updateUserId(providerId, providerKey, sessionId)
  }

  def confirm(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsDAO.updateConfirm(providerId, providerKey, true)
  }


  def findUserId(providerId: String, providerKey: String): Future[UserId] = {
    authenticationsValidator.mustFindUserId(providerId, providerKey)
  }

  def exists(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsValidator.mustExists(providerId, providerKey)
  }

}

