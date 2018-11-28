package io.github.cactacea.backend.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers._

// No action injection service

class DefaultInjectionService extends InjectionService {

  override def signedUp(authentication: Account): Future[Unit] = {
    Future.Unit
  }

  override def signedIn(authentication: Account): Future[Unit] = {
    Future.Unit
  }

  override def signedOut(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountNameUpdated(accountName: String, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def profileUpdated(displayName: String,
                              web: Option[String],
                              birthday: Option[Long],
                              location: Option[String],
                              bio: Option[String],
                              sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def profileImageUpdated(profileImageUri: Option[String], sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def passwordUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountReported(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountUnFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountMuted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountUnMuted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountBlocked(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountUnBlocked(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountUnFriended(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def commentCreated(id: CommentId, feedId: FeedId, message: String, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def commentDeleted(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def commentLiked(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def commentUnLiked(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def commentReported(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override  def feedCreated(feedId: FeedId, message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]],
                            privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def feedDeleted(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def feedLiked(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def feedUnLiked(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def devicePushTokenUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def deviceStatusUpdated(sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }


  override def friendRequestCreated(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def friendRequestDeleted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def friendRequestAccepted(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def friendRequestRejected(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupCreated(groupId: GroupId,
                            name: Option[String],
                            byInvitationOnly: Boolean,
                            privacyType: GroupPrivacyType,
                            authority: GroupAuthorityType,
                            sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupUpdated(groupId: GroupId,
                            name: Option[String],
                            byInvitationOnly: Boolean,
                            privacyType: GroupPrivacyType,
                            authority: GroupAuthorityType,
                            sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupDeleted(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupReported(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupInvitationCreated(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupInvitationDeleted(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupInvitationAccepted(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def groupInvitationRejected(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountGroupJoined(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountGroupLeft(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountGroupJoined(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def accountGroupLeft(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def mediumCreated(mediumId: MediumId, uri: String, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def messageCreated(id: MessageId,
                              groupId: GroupId,
                              message: Option[String],
                              mediumId: Option[MediumId],
                              sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

  override def messagesDeleted(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    Future.Unit
  }

}
