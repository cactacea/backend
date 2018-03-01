package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.ActionService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

@Singleton
class DefaultActionService extends ActionService {

  def accountSignedUp(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountSignedIn(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountUnFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountMuted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountUnMuted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountBlocked(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }
  def accountUnBlocked(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestCreated(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestDeleted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestAccepted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestRejected(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupCreated(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupDeleted(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupInvitationCreated(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupInvitationDeleted(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountJoined(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountLeft(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountJoined(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountLeft(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

}
