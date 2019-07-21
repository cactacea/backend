package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.domain.repositories.FriendRequestsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}

class FriendRequestsService @Inject()(
                                       db: DatabaseService,
                                       friendRequestsRepository: FriendRequestsRepository,
                                       queueService: QueueService
                                     ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      id <- db.transaction(friendRequestsRepository.create(accountId, sessionId))
      _ <- queueService.enqueueFriendRequest(id)
    } yield (id)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(friendRequestsRepository.delete(accountId, sessionId))
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsRepository.find(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(friendRequestsRepository.accept(friendRequestId, sessionId))
    } yield (())
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(friendRequestsRepository.reject(friendRequestId, sessionId))
    } yield (())
  }

}
