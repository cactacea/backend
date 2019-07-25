package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.infrastructure.dao.{FriendRequestsDAO, FriendRequestsStatusDAO, NotificationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, FriendRequestsValidator}


class FriendRequestsRepository @Inject()(
                                          accountsValidator: AccountsValidator,
                                          friendRequestsDAO: FriendRequestsDAO,
                                          friendRequestsStatusDAO: FriendRequestsStatusDAO,
                                          friendRequestsValidator: FriendRequestsValidator,
                                          friendsRepository: FriendsRepository,
                                          notificationsDAO: NotificationsDAO
                                        ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendRequestsValidator.notExist(accountId, sessionId)
      _ <- friendRequestsStatusDAO.create(accountId, sessionId)
      i <- friendRequestsDAO.create(accountId, sessionId)
      _ <- notificationsDAO.createNotification(i, accountId, sessionId)
    } yield (i)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendRequestsValidator.exist(accountId, sessionId)
      _ <- friendRequestsStatusDAO.delete(accountId, sessionId)
      _ <- friendRequestsDAO.delete(accountId, sessionId)
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsDAO.find(since, offset, count, received, sessionId)
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsValidator.find(friendRequestId, sessionId)
      _ <- friendsRepository.create(sessionId.toAccountId, f.toSessionId)
      _ <- friendRequestsStatusDAO.delete(sessionId.toAccountId, f.toSessionId)
      _ <- friendRequestsDAO.update(friendRequestId, FriendRequestStatusType.accepted, sessionId)
    } yield (())
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      f <- friendRequestsValidator.find(friendRequestId, sessionId)
      _ <- friendRequestsStatusDAO.delete(sessionId.toAccountId, f.toSessionId)
      _ <- friendRequestsDAO.update(friendRequestId, FriendRequestStatusType.rejected, sessionId).map(_ => true)
    } yield (())
  }

}
