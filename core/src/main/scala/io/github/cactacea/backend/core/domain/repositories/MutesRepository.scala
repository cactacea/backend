package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, MutesDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class MutesRepository @Inject()(
                                 accountsDAO: AccountsDAO,
                                 mutesDAO: MutesDAO
                               ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- mutesDAO.validateNotExist(accountId, sessionId)
      _ <- mutesDAO.create(accountId, sessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- mutesDAO.validateExist(accountId, sessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
    } yield (Unit)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    mutesDAO.find(since, offset, count, sessionId)
  }

}
