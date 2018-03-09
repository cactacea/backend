package io.github.cactacea.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.domain.models.Authentication
import io.github.cactacea.core.infrastructure.identifiers._

class DefaultInjectionService extends InjectionService {

  def signedUp(authentication: Authentication): Future[Unit] = {
    Future.Unit
  }

  def signedIn(authentication: Authentication): Future[Unit] = {
    Future.Unit
  }

  def signedOut(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def displayNameUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountNameUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def profileUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def profileImageUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def passwordUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountReported(accountId: AccountId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
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

  def accountUnFriended(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def commentCreated(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def commentDeleted(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def commentLiked(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def commentUnLiked(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def commentReported(commentId: CommentId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def feedCreated(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def feedDeleted(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def feedLiked(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def feedUnLiked(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def deviceUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestCreated(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestDeleted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestAccepted(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def friendRequestRejected(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupCreated(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupUpdated(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupDeleted(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupReported(groupId: GroupId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupInvitationCreated(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupInvitationDeleted(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupInvitationAccepted(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def groupInvitationRejected(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountGroupJoined(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountGroupLeft(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountGroupJoined(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def accountGroupLeft(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def mediumCreated(mediumId: MediumId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def messageCreated(messageId: MessageId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def messagesDeleted(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def socialAccountConnected(socialAccountType: String, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  def socialAccountDisconnected(socialAccountType: String, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

}
