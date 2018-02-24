package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.responses.FriendRequestCreated
import io.github.cactacea.core.domain.models.{FriendRequest}
import io.github.cactacea.core.domain.repositories.{FriendRequestsRepository}
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{FriendRequestId, SessionId, AccountId}

class FriendRequestsService @Inject()(db: DatabaseService) {

  @Inject var friendRequestsRepository: FriendRequestsRepository = _

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestCreated] = {
    friendRequestsRepository.create(
      accountId,
      sessionId
    ).map(FriendRequestCreated(_))
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsRepository.findAll(since, offset, count, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      friendRequestsRepository.accept(friendRequestId, sessionId)
    }
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      friendRequestsRepository.reject(friendRequestId, sessionId)
    }
  }

}
