package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.FollowsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}

@Singleton
class FollowsService @Inject()(
                                databaseService: DatabaseService,
                                followsRepository: FollowsRepository
                              ) {

  import databaseService._

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction {
      followsRepository.create(userId, sessionId)
    }
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction {
      followsRepository.delete(userId, sessionId)
    }
  }

  def find(userId: UserId, userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    followsRepository.find(userId, userName, since, offset, count, sessionId)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    followsRepository.find(userName, since, offset, count, sessionId)
  }

}
