package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.FriendsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}

@Singleton
class FriendsService @Inject()(
                                databaseService: DatabaseService,
                                friendsRepository: FriendsRepository
                              ) {

  import databaseService._

  def find(userId: UserId, userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    friendsRepository.find(userId, userName, since, offset, count, sessionId)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    friendsRepository.find(userName, since, offset, count, sessionId)
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction{
      friendsRepository.delete(userId, sessionId)
    }
  }

}
