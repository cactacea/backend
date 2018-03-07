package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.FollowsRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FollowsService {

  @Inject private var db: DatabaseService = _
  @Inject private var followsRepository: FollowsRepository = _
  @Inject private var actionService: InjectionService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- followsRepository.create(accountId, sessionId)
        _ <- actionService.accountFollowed(accountId, sessionId)
      } yield (r)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- followsRepository.delete(accountId, sessionId)
        _ <- actionService.accountUnFollowed(accountId, sessionId)
      } yield (r)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followsRepository.findAll(since, offset, count, sessionId)
  }


}
