package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.domain.enums.{FriendRequestStatusType, NotificationType}
import io.github.cactacea.core.domain.models.FriendRequest
import io.github.cactacea.core.infrastructure.dao.{FriendRequestsDAO, FriendRequestsStatusDAO, NotificationsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FriendRequestId, SessionId}

@Singleton
class FriendRequestsRepository {

  @Inject private var friendRequestsStatusDAO: FriendRequestsStatusDAO = _
  @Inject private var friendRequestsDAO: FriendRequestsDAO = _
  @Inject private var notificationsDAO: NotificationsDAO = _
  @Inject private var friendsRepository: FriendsRepository = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.notExistFriendRequest(accountId, sessionId)
      _ <- friendRequestsStatusDAO.create(accountId, sessionId)
      id <- friendRequestsDAO.create(accountId, sessionId)
      _ <- notificationsDAO.create(accountId, NotificationType.friendRequest, id.value)
    } yield (id)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existFriendRequest(accountId, sessionId)
      _ <- friendRequestsStatusDAO.delete(accountId, sessionId)
      _ <- friendRequestsDAO.delete(accountId, sessionId)
    } yield (Future.value(Unit))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], received: Boolean, sessionId: SessionId): Future[List[FriendRequest]] = {
    friendRequestsDAO.findAll(since, offset, count, received, sessionId).map(_.map(t =>
      FriendRequest(t._1, t._2, t._3)
    ))
  }

  def accept(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- validationDAO.findFriendRequest(friendRequestId, sessionId)
      _ <- friendsRepository.create(sessionId.toAccountId, r.by.toSessionId)
      _ <- friendRequestsStatusDAO.delete(sessionId.toAccountId, r.by.toSessionId)
      _ <- friendRequestsDAO.update(friendRequestId, FriendRequestStatusType.accepted, sessionId)
    } yield (Future.value(Unit))
  }

  def reject(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- validationDAO.findFriendRequest(friendRequestId, sessionId)
      _ <- friendRequestsStatusDAO.delete(sessionId.toAccountId, r.by.toSessionId)
      _ <- friendRequestsDAO.update(friendRequestId, FriendRequestStatusType.rejected, sessionId).map(_ => true)
    } yield (Future.value(Unit))
  }

}
