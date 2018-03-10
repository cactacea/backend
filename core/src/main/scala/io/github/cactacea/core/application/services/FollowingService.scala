package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.FollowingRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowingService {

  @Inject private var db: DatabaseService = _
  @Inject private var followingRepository: FollowingRepository = _
  @Inject private var actionService: InjectionService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- followingRepository.create(accountId, sessionId)
        _ <- actionService.accountFollowed(accountId, sessionId)
      } yield (r)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- followingRepository.delete(accountId, sessionId)
        _ <- actionService.accountUnFollowed(accountId, sessionId)
      } yield (r)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followingRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followingRepository.findAll(since, offset, count, sessionId)
  }


}
