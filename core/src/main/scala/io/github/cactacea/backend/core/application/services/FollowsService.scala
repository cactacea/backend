package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.FollowsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowsService @Inject()(
                                db: DatabaseService,
                                followsRepository: FollowsRepository,
                                actionService: InjectionService
                              ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- followsRepository.create(accountId, sessionId)
        _ <- actionService.accountFollowed(accountId, sessionId)
      } yield (Unit)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- followsRepository.delete(accountId, sessionId)
        _ <- actionService.accountUnFollowed(accountId, sessionId)
      } yield (Unit)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.findAll(since, offset, count, sessionId)
  }


}
