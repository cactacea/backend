package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{InjectionService, EnqueueService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.domain.repositories.FriendRequestsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}

class FriendRequestsService @Inject()(
                                       db: DatabaseService,
                                       friendRequestsRepository: FriendRequestsRepository,
                                       publishService: EnqueueService,
                                       actionService: InjectionService
                                     ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    db.transaction {
      for {
        id <- friendRequestsRepository.create(accountId, sessionId)
        _ <- actionService.friendRequestCreated(accountId, sessionId)
        _ <- publishService.enqueueFriendRequest(id)
      } yield (id)
    }
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- friendRequestsRepository.delete(accountId, sessionId)
        _ <- actionService.friendRequestDeleted(accountId, sessionId)
      } yield (r)
    }
  }

  def findAll(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsRepository.findAll(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- friendRequestsRepository.accept(friendRequestId, sessionId)
        _ <- actionService.friendRequestAccepted(friendRequestId, sessionId)
      } yield (r)
    }
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- friendRequestsRepository.reject(friendRequestId, sessionId)
        _ <- actionService.friendRequestRejected(friendRequestId, sessionId)
      } yield (r)
    }
  }

}
