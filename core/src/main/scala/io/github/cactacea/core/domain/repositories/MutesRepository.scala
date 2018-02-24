package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.factories.AccountFactory
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao.{MutesDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class MutesRepository {

  @Inject var mutesDAO: MutesDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.notExistMutes(accountId, sessionId)
      _ <- mutesDAO.create(accountId, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.existMutes(accountId, sessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
    } yield (Future.value(Unit))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    mutesDAO.findAll(sessionId.toAccountId, since, offset, count, sessionId)
      .map(_.map(t => AccountFactory.create(t._1, t._2)))
  }

}
