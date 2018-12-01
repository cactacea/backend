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
                                followerRepository: FollowsRepository,
                                actionService: InjectionService
                              ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- followerRepository.create(accountId, sessionId)
        _ <- actionService.accountFollowed(accountId, sessionId)
      } yield (r)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- followerRepository.delete(accountId, sessionId)
        _ <- actionService.accountUnFollowed(accountId, sessionId)
      } yield (r)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followerRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followerRepository.findAll(since, offset, count, sessionId)
  }


}
