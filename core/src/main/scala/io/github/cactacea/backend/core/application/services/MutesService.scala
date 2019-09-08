package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.MutesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}

@Singleton
class MutesService @Inject()(
                              databaseService: DatabaseService,
                              mutesRepository: MutesRepository
                            ) {

  import databaseService._
  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction {
      mutesRepository.create(userId, sessionId)
    }
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction {
      mutesRepository.delete(userId, sessionId)
    }
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[Seq[User]]= {
    mutesRepository.find(userName, since, offset, count, sessionId)
  }

}
