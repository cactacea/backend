package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.AccountDetail
import io.github.cactacea.backend.core.infrastructure.identifiers._

trait InjectionService {
  def signedUp(authentication: AccountDetail): Future[Unit]
  def signedIn(authentication: AccountDetail): Future[Unit]
  def signedOut(sessionId: SessionId): Future[Unit]
  def accountNameUpdated(accountName: String, sessionId: SessionId): Future[Unit]
  def profileUpdated(displayName: String, web: Option[String], birthday: Option[Long], location: Option[String],
                     bio: Option[String], sessionId: SessionId): Future[Unit]
  def profileImageUpdated(profileImageUri: Option[String], sessionId: SessionId): Future[Unit]
  def passwordUpdated(sessionId: SessionId): Future[Unit]
  def devicePushTokenUpdated(sessionId: SessionId): Future[Unit]
  def deviceStatusUpdated(sessionId: SessionId): Future[Unit]
  def accountReported(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit]
  def accountFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def accountUnFollowed(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def accountUnFriended(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def accountMuted(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def accountUnMuted(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def accountBlocked(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def accountUnBlocked(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def commentCreated(id: CommentId, feedId: FeedId, message: String, sessionId: SessionId): Future[Unit]
  def commentDeleted(commentId: CommentId, sessionId: SessionId): Future[Unit]
  def commentLiked(commentId: CommentId, sessionId: SessionId): Future[Unit]
  def commentUnLiked(commentId: CommentId, sessionId: SessionId): Future[Unit]
  def commentReported(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit]
  def feedCreated(feedId: FeedId, message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]],
                  privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], sessionId: SessionId): Future[Unit]
  def feedDeleted(feedId: FeedId, sessionId: SessionId): Future[Unit]
  def feedLiked(feedId: FeedId, sessionId: SessionId): Future[Unit]
  def feedUnLiked(feedId: FeedId, sessionId: SessionId): Future[Unit]
  def friendRequestCreated(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def friendRequestDeleted(accountId: AccountId, sessionId: SessionId): Future[Unit]
  def friendRequestAccepted(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit]
  def friendRequestRejected(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Unit]
  def groupCreated(groupId: GroupId, name: Option[String], byInvitationOnly: Boolean,
                   privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[Unit]
  def groupUpdated(groupId: GroupId, name: Option[String], invitationOnly: Boolean,
                   privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[Unit]
  def groupDeleted(groupId: GroupId, sessionId: SessionId): Future[Unit]
  def groupReported(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit]
  def groupInvitationCreated(accountId: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[Unit]
  def groupInvitationDeleted(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit]
  def groupInvitationAccepted(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit]
  def groupInvitationRejected(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit]
  def accountGroupJoined(groupId: GroupId, sessionId: SessionId): Future[Unit]
  def accountGroupLeft(groupId: GroupId, sessionId: SessionId): Future[Unit]
  def accountGroupJoined(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit]
  def accountGroupLeft(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit]
  def mediumCreated(mediumId: MediumId, uri: String, sessionId: SessionId): Future[Unit]
  def messageCreated(id: MessageId, groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[Unit]
  def messagesDeleted(groupId: GroupId, sessionId: SessionId): Future[Unit]
}
