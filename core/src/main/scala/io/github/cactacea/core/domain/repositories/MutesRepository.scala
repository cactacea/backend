package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao.{MutesDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class MutesRepository {

  @Inject private var mutesDAO: MutesDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.notExistMute(accountId, sessionId)
      _ <- mutesDAO.create(accountId, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existMute(accountId, sessionId)
      _ <- mutesDAO.delete(accountId, sessionId)
    } yield (Future.value(Unit))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    mutesDAO.findAll(sessionId.toAccountId, since, offset, count, sessionId)
      .map(_.map({ case (a, r, n) => Account(a, r, n)}))
  }

}
