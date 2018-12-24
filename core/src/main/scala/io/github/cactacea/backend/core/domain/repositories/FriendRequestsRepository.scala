package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, FriendRequestsDAO, FriendRequestsStatusDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}

@Singleton
class FriendRequestsRepository @Inject()(
                                          accountsDAO: AccountsDAO,
                                          friendRequestsStatusDAO: FriendRequestsStatusDAO,
                                          friendRequestsDAO: FriendRequestsDAO,
                                          friendsRepository: FriendsRepository
                                        ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendRequestsDAO.validateNotExist(accountId, sessionId)
      _ <- friendRequestsStatusDAO.create(accountId, sessionId)
      id <- friendRequestsDAO.create(accountId, sessionId)
    } yield (id)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendRequestsDAO.validateExist(accountId, sessionId)
      _ <- friendRequestsStatusDAO.delete(accountId, sessionId)
      _ <- friendRequestsDAO.delete(accountId, sessionId)
    } yield (Unit)
  }

  def find(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsDAO.find(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsDAO.findOwner(friendRequestId, sessionId)
      _ <- friendsRepository.create(sessionId.toAccountId, f.toSessionId)
      _ <- friendRequestsStatusDAO.delete(sessionId.toAccountId, f.toSessionId)
      _ <- friendRequestsDAO.update(friendRequestId, FriendRequestStatusType.accepted, sessionId)
    } yield (Unit)
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsDAO.findOwner(friendRequestId, sessionId)
      _ <- friendRequestsStatusDAO.delete(sessionId.toAccountId, f.toSessionId)
      _ <- friendRequestsDAO.update(friendRequestId, FriendRequestStatusType.rejected, sessionId).map(_ => true)
    } yield (Unit)
  }

}
