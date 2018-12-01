package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.MutesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class MutesService @Inject()(
                              db: DatabaseService,
                              mutesRepository: MutesRepository,
                              injectionService: InjectionService
                            ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- mutesRepository.create(accountId, sessionId)
        _ <- injectionService.accountMuted(accountId, sessionId)
      } yield (r)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- mutesRepository.delete(accountId, sessionId)
        _ <- injectionService.accountUnMuted(accountId, sessionId)
      } yield (r)
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    mutesRepository.findAll(since, offset, count, sessionId)
  }

}
