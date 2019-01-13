package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{ListenerService, QueueService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.domain.repositories.FriendRequestsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}

class FriendRequestsService @Inject()(
                                       db: DatabaseService,
                                       friendRequestsRepository: FriendRequestsRepository,
                                       queueService: QueueService,
                                       listenerService: ListenerService
                                     ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      id <- db.transaction(friendRequestsRepository.create(accountId, sessionId))
      _ <- listenerService.friendRequestCreated(accountId, sessionId)
      _ <- queueService.enqueueFriendRequest(id)
    } yield (id)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(friendRequestsRepository.delete(accountId, sessionId))
      _ <- listenerService.friendRequestDeleted(accountId, sessionId)
    } yield (Unit)
  }

  def find(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsRepository.find(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(friendRequestsRepository.accept(friendRequestId, sessionId))
      _ <- listenerService.friendRequestAccepted(friendRequestId, sessionId)
    } yield (Unit)
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(friendRequestsRepository.reject(friendRequestId, sessionId))
      _ <- listenerService.friendRequestRejected(friendRequestId, sessionId)
    } yield (Unit)
  }

}
