package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.MutesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, MutesValidator}


class MutesRepository @Inject()(
                                 accountsValidator: AccountsValidator,
                                 mutesValidator: MutesValidator,
                                 mutesDAO: MutesDAO
                               ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- mutesValidator.notExist(accountId, sessionId)
      _ <- mutesDAO.create(accountId, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- mutesValidator.exist(accountId, sessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    mutesDAO.find(since, offset, count, sessionId)
  }

}
