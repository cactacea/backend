package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.MutesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, MutesValidator}


class MutesRepository @Inject()(
                                 accountsValidator: AccountsValidator,
                                 mutesDAO: MutesDAO,
                                 mutesValidator: MutesValidator
                               ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- mutesValidator.mustNotMuted(accountId, sessionId)
      _ <- mutesDAO.create(accountId, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- mutesValidator.mustMuted(accountId, sessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
    } yield (())
  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    mutesDAO.find(accountName, since, offset, count, sessionId)
  }

}
