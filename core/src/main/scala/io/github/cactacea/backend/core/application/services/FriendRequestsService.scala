package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.domain.repositories.FriendRequestsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{FriendRequestId, SessionId, UserId}

@Singleton
class FriendRequestsService @Inject()(
                                       databaseService: DatabaseService,
                                       friendRequestsRepository: FriendRequestsRepository,
                                       queueService: QueueService
                                     ) {

  import databaseService._

  def create(userId: UserId, sessionId: SessionId): Future[FriendRequestId] = {
    transaction {
      for {
        i <- friendRequestsRepository.create(userId, sessionId)
        _ <- queueService.enqueueFriendRequest(i)
      } yield (i)
    }
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    transaction {
      friendRequestsRepository.delete(userId, sessionId)
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[Seq[FriendRequest]] = {
    friendRequestsRepository.find(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    transaction {
      friendRequestsRepository.accept(friendRequestId, sessionId)
    }
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    transaction {
      friendRequestsRepository.reject(friendRequestId, sessionId)
    }
  }

}
