package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.MutesRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class MutesService @Inject()(db: DatabaseService) {

  @Inject var mutesRepository: MutesRepository = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      mutesRepository.create(accountId, sessionId)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      mutesRepository.delete(accountId, sessionId)
    }
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    mutesRepository.findAll(since, offset, count, sessionId)
  }

}
