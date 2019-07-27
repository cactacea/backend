package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, AuthenticationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Authentications
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, AuthenticationsValidator}


class AuthenticationsRepository @Inject()(
                                           accountsDAO: AccountsDAO,
                                           accountsValidator: AccountsValidator,
                                           authenticationsDAO: AuthenticationsDAO,
                                           authenticationsValidator: AuthenticationsValidator
                                  ) {

  def find(providerId: String, providerKey: String): Future[Option[Authentications]] = {
    authenticationsDAO.find(providerId, providerKey)
  }

  def updateAccountName(providerId: String, providerKey: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotExist(providerKey, sessionId)
      _ <- accountsDAO.updateAccountName(providerKey, sessionId)
      _ <- authenticationsDAO.updateProviderKey(providerId, providerKey, sessionId)
    } yield (())
  }

  def link(providerId: String, providerKey: String, sessionId: SessionId): Future[Unit] = {
    authenticationsDAO.updateAccountId(providerId, providerKey, sessionId)
  }

  def confirm(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsDAO.updateConfirm(providerId, providerKey, true)
  }


  def findAccountId(providerId: String, providerKey: String): Future[AccountId] = {
    authenticationsValidator.mustFindAccountId(providerId, providerKey)
  }

  def exists(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsValidator.mustExists(providerId, providerKey)
  }

}

