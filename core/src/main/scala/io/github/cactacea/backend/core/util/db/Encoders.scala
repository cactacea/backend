package io.github.cactacea.backend.core.util.db

import java.util.Date

import io.getquill.context.sql.SqlContext
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import org.joda.time.DateTime

trait Encoders {
  this: SqlContext[_, _] =>

  implicit val datetimeDecode: MappedEncoding[Date, DateTime] = MappedEncoding[Date, DateTime] (date => new DateTime(date.getTime()))
  implicit val datetimeEncode: MappedEncoding[DateTime, Date] = MappedEncoding[DateTime, Date] (dateTime => dateTime.toDate)

  implicit val userIdDecode: MappedEncoding[UserId, Long] = MappedEncoding[UserId, Long] (id => id.onlyValue)
  implicit val userIdEncode: MappedEncoding[Long, UserId] = MappedEncoding[Long, UserId] (long => UserId(long))
  implicit val userChannelIdDecode: MappedEncoding[UserChannelId, Long] = MappedEncoding[UserChannelId, Long] (id => id.onlyValue)
  implicit val userChannelIdEncode: MappedEncoding[Long, UserChannelId] = MappedEncoding[Long, UserChannelId] (long => UserChannelId(long))
  implicit val userReportIdDecode: MappedEncoding[UserReportId, Long] = MappedEncoding[UserReportId, Long] (id => id.onlyValue)
  implicit val userReportIdEncode: MappedEncoding[Long, UserReportId] = MappedEncoding[Long, UserReportId] (long => UserReportId(long))
  implicit val blockIdDecode: MappedEncoding[BlockId, Long] = MappedEncoding[BlockId, Long] (id => id.onlyValue)
  implicit val blockIdEncode: MappedEncoding[Long, BlockId] = MappedEncoding[Long, BlockId] (long => BlockId(long))
  implicit val muteIdDecode: MappedEncoding[MuteId, Long] = MappedEncoding[MuteId, Long] (id => id.onlyValue)
  implicit val muteIdEncode: MappedEncoding[Long, MuteId] = MappedEncoding[Long, MuteId] (long => MuteId(long))
  implicit val followIdDecode: MappedEncoding[FollowId, Long] = MappedEncoding[FollowId, Long] (id => id.onlyValue)
  implicit val followIdEncode: MappedEncoding[Long, FollowId] = MappedEncoding[Long, FollowId] (long => FollowId(long))
  implicit val followerIdDecode: MappedEncoding[FollowerId, Long] = MappedEncoding[FollowerId, Long] (id => id.onlyValue)
  implicit val followerIdEncode: MappedEncoding[Long, FollowerId] = MappedEncoding[Long, FollowerId] (long => FollowerId(long))
  implicit val commentIdDecode: MappedEncoding[CommentId, Long] = MappedEncoding[CommentId, Long] (id => id.onlyValue)
  implicit val commentIdEncode: MappedEncoding[Long, CommentId] = MappedEncoding[Long, CommentId] (long => CommentId(long))
  implicit val commentLikeIdDecode: MappedEncoding[CommentLikeId, Long] = MappedEncoding[CommentLikeId, Long] (id => id.onlyValue)
  implicit val commentLikeIdEncode: MappedEncoding[Long, CommentLikeId] = MappedEncoding[Long, CommentLikeId] (long => CommentLikeId(long))
  implicit val commentReportIdDecode: MappedEncoding[CommentReportId, Long] = MappedEncoding[CommentReportId, Long] (id => id.onlyValue)
  implicit val commentReportIdEncode: MappedEncoding[Long, CommentReportId] = MappedEncoding[Long, CommentReportId] (long => CommentReportId(long))
  implicit val deviceIdDecode: MappedEncoding[DeviceId, Long] = MappedEncoding[DeviceId, Long] (id => id.onlyValue)
  implicit val deviceIdEncode: MappedEncoding[Long, DeviceId] = MappedEncoding[Long, DeviceId] (long => DeviceId(long))
  implicit val tweetIdDecode: MappedEncoding[TweetId, Long] = MappedEncoding[TweetId, Long] (id => id.onlyValue)
  implicit val tweetIdEncode: MappedEncoding[Long, TweetId] = MappedEncoding[Long, TweetId] (long => TweetId(long))
  implicit val tweetLikeIdDecode: MappedEncoding[TweetLikeId, Long] = MappedEncoding[TweetLikeId, Long] (id => id.onlyValue)
  implicit val tweetLikeIdEncode: MappedEncoding[Long, TweetLikeId] = MappedEncoding[Long, TweetLikeId] (long => TweetLikeId(long))
  implicit val tweetReportIdDecode: MappedEncoding[TweetReportId, Long] = MappedEncoding[TweetReportId, Long] (id => id.onlyValue)
  implicit val tweetReportIdEncode: MappedEncoding[Long, TweetReportId] = MappedEncoding[Long, TweetReportId] (long => TweetReportId(long))
  implicit val friendIdDecode: MappedEncoding[FriendId, Long] = MappedEncoding[FriendId, Long] (id => id.onlyValue)
  implicit val friendIdEncode: MappedEncoding[Long, FriendId] = MappedEncoding[Long, FriendId] (long => FriendId(long))
  implicit val friendRequestIdDecode: MappedEncoding[FriendRequestId, Long] = MappedEncoding[FriendRequestId, Long] (id => id.onlyValue)
  implicit val friendRequestIdEncode: MappedEncoding[Long, FriendRequestId] = MappedEncoding[Long, FriendRequestId] (long => FriendRequestId(long))
  implicit val channelIdDecode: MappedEncoding[ChannelId, Long] = MappedEncoding[ChannelId, Long] (id => id.onlyValue)
  implicit val channelIdEncode: MappedEncoding[Long, ChannelId] = MappedEncoding[Long, ChannelId] (long => ChannelId(long))
  implicit val channelReportIdDecode: MappedEncoding[ChannelReportId, Long] = MappedEncoding[ChannelReportId, Long] (id => id.onlyValue)
  implicit val channelReportIdEncode: MappedEncoding[Long, ChannelReportId] = MappedEncoding[Long, ChannelReportId] (long => ChannelReportId(long))
  implicit val invitationIdDecode: MappedEncoding[InvitationId, Long] = MappedEncoding[InvitationId, Long] (id => id.onlyValue)
  implicit val invitationIdEncode: MappedEncoding[Long, InvitationId] = MappedEncoding[Long, InvitationId] (long => InvitationId(long))
  implicit val mediumIdDecode: MappedEncoding[MediumId, Long] = MappedEncoding[MediumId, Long] (id => id.onlyValue)
  implicit val mediumIdEncode: MappedEncoding[Long, MediumId] = MappedEncoding[Long, MediumId] (long => MediumId(long))
  implicit val messageIdDecode: MappedEncoding[MessageId, Long] = MappedEncoding[MessageId, Long] (id => id.onlyValue)
  implicit val messageIdEncode: MappedEncoding[Long, MessageId] = MappedEncoding[Long, MessageId] (long => MessageId(long))
  implicit val notificationIdDecode: MappedEncoding[NotificationId, Long] = MappedEncoding[NotificationId, Long] (id => id.onlyValue)
  implicit val notificationIdEncode: MappedEncoding[Long, NotificationId] = MappedEncoding[Long, NotificationId] (long => NotificationId(long))
  implicit val sessionIdDecode: MappedEncoding[SessionId, Long] = MappedEncoding[SessionId, Long] (id => id.onlyValue)
  implicit val sessionIdEncode: MappedEncoding[Long, SessionId] = MappedEncoding[Long, SessionId] (long => SessionId(long))
  implicit val stampIdDecode: MappedEncoding[StampId, Long] = MappedEncoding[StampId, Long] (id => id.onlyValue)
  implicit val stampIdEncode: MappedEncoding[Long, StampId] = MappedEncoding[Long, StampId] (long => StampId(long))

  implicit val userStatusDecode: MappedEncoding[UserStatusType, Byte] = MappedEncoding[UserStatusType, Byte] (enumValue => enumValue.value)
  implicit val userStatusEncode: MappedEncoding[Byte, UserStatusType] = MappedEncoding[Byte, UserStatusType] (long => UserStatusType.forName(long))
  implicit val tweetPrivacyTypeDecode: MappedEncoding[TweetPrivacyType, Byte] = MappedEncoding[TweetPrivacyType, Byte] (enumValue => enumValue.value)
  implicit val tweetPrivacyTypeEncode: MappedEncoding[Byte, TweetPrivacyType] = MappedEncoding[Byte, TweetPrivacyType] (long => TweetPrivacyType.forName(long))
  implicit val channelAuthorityTypeDecode: MappedEncoding[ChannelAuthorityType, Byte] = MappedEncoding[ChannelAuthorityType, Byte] (enumValue => enumValue.value)
  implicit val channelAuthorityTypeEncode: MappedEncoding[Byte, ChannelAuthorityType]
  = MappedEncoding[Byte, ChannelAuthorityType] (long => ChannelAuthorityType.forName(long))
  implicit val channelPrivacyTypeDecode: MappedEncoding[ChannelPrivacyType, Byte] = MappedEncoding[ChannelPrivacyType, Byte] (enumValue => enumValue.value)
  implicit val channelPrivacyTypeEncode: MappedEncoding[Byte, ChannelPrivacyType] = MappedEncoding[Byte, ChannelPrivacyType] (long => ChannelPrivacyType.forName(long))
  implicit val mediumTypeDecode: MappedEncoding[MediumType, Byte] = MappedEncoding[MediumType, Byte] (enumValue => enumValue.value)
  implicit val mediumTypeEncode: MappedEncoding[Byte, MediumType] = MappedEncoding[Byte, MediumType] (long => MediumType.forName(long))
  implicit val messageTypeDecode: MappedEncoding[MessageType, Byte] = MappedEncoding[MessageType, Byte] (enumValue => enumValue.value)
  implicit val messageTypeEncode: MappedEncoding[Byte, MessageType] = MappedEncoding[Byte, MessageType] (long => MessageType.forName(long))
  implicit val notificationTypeDecode: MappedEncoding[NotificationType, Byte] = MappedEncoding[NotificationType, Byte] (enumValue => enumValue.value)
  implicit val notificationTypeEncode: MappedEncoding[Byte, NotificationType] = MappedEncoding[Byte, NotificationType] (long => NotificationType.forName(long))
  implicit val pushNotificationTypeDecode: MappedEncoding[PushNotificationType, Byte]
  = MappedEncoding[PushNotificationType, Byte] (enumValue => enumValue.value)
  implicit val pushNotificationTypeEncode: MappedEncoding[Byte, PushNotificationType]
  = MappedEncoding[Byte, PushNotificationType] (long => PushNotificationType.forName(long))
  implicit val reportTypeDecode: MappedEncoding[ReportType, Byte] = MappedEncoding[ReportType, Byte] (enumValue => enumValue.value)
  implicit val reportTypeEncode: MappedEncoding[Byte, ReportType] = MappedEncoding[Byte, ReportType] (long => ReportType.forName(long))
  implicit val contentStatusTypeDecode: MappedEncoding[ContentStatusType, Byte] = MappedEncoding[ContentStatusType, Byte] (enumValue => enumValue.value)
  implicit val contentStatusTypeEncode: MappedEncoding[Byte, ContentStatusType]
  = MappedEncoding[Byte, ContentStatusType] (long => ContentStatusType.forName(long))
  implicit val deviceTypeDecode: MappedEncoding[DeviceType, Byte] = MappedEncoding[DeviceType, Byte] (enumValue => enumValue.value)
  implicit val deviceTypeEncode: MappedEncoding[Byte, DeviceType] = MappedEncoding[Byte, DeviceType] (long => DeviceType.forName(long))
  implicit val activeStatusTypeDecode: MappedEncoding[ActiveStatusType, Byte] = MappedEncoding[ActiveStatusType, Byte] (enumValue => enumValue.value)
  implicit val activeStatusTypeEncode: MappedEncoding[Byte, ActiveStatusType] = MappedEncoding[Byte, ActiveStatusType] (long => ActiveStatusType.forName(long))

}
