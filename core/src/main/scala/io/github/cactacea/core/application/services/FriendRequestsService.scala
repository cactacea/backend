package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.FriendRequest
import io.github.cactacea.core.domain.repositories.{FriendRequestsRepository, NotificationsRepository}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}

class FriendRequestsService {

  @Inject private var db: DatabaseService = _
  @Inject private var friendRequestsRepository: FriendRequestsRepository = _
  @Inject private var notificationsRepository: NotificationsRepository = _
  @Inject private var actionService: InjectionService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    db.transaction {
      for {
        id <- friendRequestsRepository.create(accountId, sessionId)
        _ <- actionService.friendRequestCreated(accountId, sessionId)
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

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
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
