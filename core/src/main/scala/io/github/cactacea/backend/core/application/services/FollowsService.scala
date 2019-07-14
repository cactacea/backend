package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.FollowsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowsService @Inject()(
                                   db: DatabaseService,
                                   followsRepository: FollowsRepository,
                                   listenerService: ListenerService
                              ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(followsRepository.create(accountId, sessionId))
      _ <- listenerService.accountFollowed(accountId, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(followsRepository.delete(accountId, sessionId))
      _ <- listenerService.accountUnFollowed(accountId, sessionId)
    } yield (())
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.find(since, offset, count, sessionId)
  }


}
