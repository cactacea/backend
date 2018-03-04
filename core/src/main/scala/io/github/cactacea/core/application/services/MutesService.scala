package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.MutesRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class MutesService @Inject()(db: DatabaseService, mutesRepository: MutesRepository, injectionService: InjectionService) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(mutesRepository.create(accountId, sessionId))
      _ <- injectionService.accountMuted(accountId, sessionId)
    } yield (r)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(mutesRepository.delete(accountId, sessionId))
      _ <- injectionService.accountUnMuted(accountId, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    mutesRepository.findAll(since, offset, count, sessionId)
  }

}
