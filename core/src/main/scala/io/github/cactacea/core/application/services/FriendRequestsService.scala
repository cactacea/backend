package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.FriendRequest
import io.github.cactacea.core.domain.repositories.FriendRequestsRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

class FriendRequestsService @Inject()(db: DatabaseService, friendRequestsRepository: FriendRequestsRepository, actionService: InjectionService) {

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      id <- db.transaction(friendRequestsRepository.create(accountId, sessionId))
      _ <- actionService.friendRequestCreated(accountId, sessionId)
    } yield (id)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(friendRequestsRepository.delete(accountId, sessionId))
      _ <- actionService.friendRequestDeleted(accountId, sessionId)
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsRepository.findAll(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(friendRequestsRepository.accept(friendRequestId, sessionId))
      _ <- actionService.friendRequestAccepted(friendRequestId, sessionId)
    } yield (r)
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(friendRequestsRepository.reject(friendRequestId, sessionId))
      _ <- actionService.friendRequestRejected(friendRequestId, sessionId)
    } yield (r)
  }

}
