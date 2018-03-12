package io.github.cactacea.core.application.components.services

import java.util.Date

import com.typesafe.config.Config
import io.getquill._
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers._
import org.joda.time.DateTime

class DatabaseService(config: Config) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config) {

  // java objects
  implicit val DatetimeDecode: MappedEncoding[Date, DateTime] = MappedEncoding[Date, DateTime] (date => new DateTime(date.getTime()))
  implicit val DatetimeEncode: MappedEncoding[DateTime, Date] = MappedEncoding[DateTime, Date] (dateTime => dateTime.toDate)

  implicit val AccountIdDecode: MappedEncoding[AccountId, Long] = MappedEncoding[AccountId, Long] (id => id.onlyValue)
  implicit val AccountIdEncode: MappedEncoding[Long, AccountId] = MappedEncoding[Long, AccountId] (long => AccountId(long))
  implicit val AccountGroupIdDecode: MappedEncoding[AccountGroupId, Long] = MappedEncoding[AccountGroupId, Long] (id => id.onlyValue)
  implicit val AccountGroupIdEncode: MappedEncoding[Long, AccountGroupId] = MappedEncoding[Long, AccountGroupId] (long => AccountGroupId(long))
  implicit val AccountReportIdDecode: MappedEncoding[AccountReportId, Long] = MappedEncoding[AccountReportId, Long] (id => id.onlyValue)
  implicit val AccountReportIdEncode: MappedEncoding[Long, AccountReportId] = MappedEncoding[Long, AccountReportId] (long => AccountReportId(long))
  implicit val BlockIdDecode: MappedEncoding[BlockId, Long] = MappedEncoding[BlockId, Long] (id => id.onlyValue)
  implicit val BlockIdEncode: MappedEncoding[Long, BlockId] = MappedEncoding[Long, BlockId] (long => BlockId(long))
  implicit val CommentIdDecode: MappedEncoding[CommentId, Long] = MappedEncoding[CommentId, Long] (id => id.onlyValue)
  implicit val CommentIdEncode: MappedEncoding[Long, CommentId] = MappedEncoding[Long, CommentId] (long => CommentId(long))
  implicit val CommentLikeIdDecode: MappedEncoding[CommentLikeId, Long] = MappedEncoding[CommentLikeId, Long] (id => id.onlyValue)
  implicit val CommentLikeIdEncode: MappedEncoding[Long, CommentLikeId] = MappedEncoding[Long, CommentLikeId] (long => CommentLikeId(long))
  implicit val CommentReportIdDecode: MappedEncoding[CommentReportId, Long] = MappedEncoding[CommentReportId, Long] (id => id.onlyValue)
  implicit val CommentReportIdEncode: MappedEncoding[Long, CommentReportId] = MappedEncoding[Long, CommentReportId] (long => CommentReportId(long))
  implicit val DeviceIdDecode: MappedEncoding[DeviceId, Long] = MappedEncoding[DeviceId, Long] (id => id.onlyValue)
  implicit val DeviceIdEncode: MappedEncoding[Long, DeviceId] = MappedEncoding[Long, DeviceId] (long => DeviceId(long))
  implicit val FeedIdDecode: MappedEncoding[FeedId, Long] = MappedEncoding[FeedId, Long] (id => id.onlyValue)
  implicit val FeedIdEncode: MappedEncoding[Long, FeedId] = MappedEncoding[Long, FeedId] (long => FeedId(long))
  implicit val FeedLikeIdDecode: MappedEncoding[FeedLikeId, Long] = MappedEncoding[FeedLikeId, Long] (id => id.onlyValue)
  implicit val FeedLikeIdEncode: MappedEncoding[Long, FeedLikeId] = MappedEncoding[Long, FeedLikeId] (long => FeedLikeId(long))
  implicit val FeedReportIdDecode: MappedEncoding[FeedReportId, Long] = MappedEncoding[FeedReportId, Long] (id => id.onlyValue)
  implicit val FeedReportIdEncode: MappedEncoding[Long, FeedReportId] = MappedEncoding[Long, FeedReportId] (long => FeedReportId(long))
  implicit val FriendRequestIdDecode: MappedEncoding[FriendRequestId, Long] = MappedEncoding[FriendRequestId, Long] (id => id.onlyValue)
  implicit val FriendRequestIdEncode: MappedEncoding[Long, FriendRequestId] = MappedEncoding[Long, FriendRequestId] (long => FriendRequestId(long))
  implicit val GroupIdDecode: MappedEncoding[GroupId, Long] = MappedEncoding[GroupId, Long] (id => id.onlyValue)
  implicit val GroupIdEncode: MappedEncoding[Long, GroupId] = MappedEncoding[Long, GroupId] (long => GroupId(long))
  implicit val GroupInvitationIdDecode: MappedEncoding[GroupInvitationId, Long] = MappedEncoding[GroupInvitationId, Long] (id => id.onlyValue)
  implicit val GroupInvitationIdEncode: MappedEncoding[Long, GroupInvitationId] = MappedEncoding[Long, GroupInvitationId] (long => GroupInvitationId(long))
  implicit val GroupReportIdDecode: MappedEncoding[GroupReportId, Long] = MappedEncoding[GroupReportId, Long] (id => id.onlyValue)
  implicit val GroupReportIdEncode: MappedEncoding[Long, GroupReportId] = MappedEncoding[Long, GroupReportId] (long => GroupReportId(long))
  implicit val MediumIdDecode: MappedEncoding[MediumId, Long] = MappedEncoding[MediumId, Long] (id => id.onlyValue)
  implicit val MediumIdEncode: MappedEncoding[Long, MediumId] = MappedEncoding[Long, MediumId] (long => MediumId(long))
  implicit val MessageIdDecode: MappedEncoding[MessageId, Long] = MappedEncoding[MessageId, Long] (id => id.onlyValue)
  implicit val MessageIdEncode: MappedEncoding[Long, MessageId] = MappedEncoding[Long, MessageId] (long => MessageId(long))
  implicit val NotificationIdDecode: MappedEncoding[NotificationId, Long] = MappedEncoding[NotificationId, Long] (id => id.onlyValue)
  implicit val NotificationIdEncode: MappedEncoding[Long, NotificationId] = MappedEncoding[Long, NotificationId] (long => NotificationId(long))
  implicit val SessionIdDecode: MappedEncoding[SessionId, Long] = MappedEncoding[SessionId, Long] (id => id.onlyValue)
  implicit val SessionIdEncode: MappedEncoding[Long, SessionId] = MappedEncoding[Long, SessionId] (long => SessionId(long))
  implicit val StampIdDecode: MappedEncoding[StampId, Long] = MappedEncoding[StampId, Long] (id => id.onlyValue)
  implicit val StampIdEncode: MappedEncoding[Long, StampId] = MappedEncoding[Long, StampId] (long => StampId(long))
  implicit val TimelineFeedIdDecode: MappedEncoding[TimelineFeedId, Long] = MappedEncoding[TimelineFeedId, Long] (id => id.onlyValue)
  implicit val TimelineFeedIdEncode: MappedEncoding[Long, TimelineFeedId] = MappedEncoding[Long, TimelineFeedId] (long => TimelineFeedId(long))

  implicit val AccountStatusDecode: MappedEncoding[AccountStatusType, Byte] = MappedEncoding[AccountStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val AccountStatusEncode: MappedEncoding[Byte, AccountStatusType] = MappedEncoding[Byte, AccountStatusType] (long => AccountStatusType.forName(long))
  implicit val FeedPrivacyTypeDecode: MappedEncoding[FeedPrivacyType, Byte] = MappedEncoding[FeedPrivacyType, Byte] (enumValue => enumValue.toValue)
  implicit val FeedPrivacyTypeEncode: MappedEncoding[Byte, FeedPrivacyType] = MappedEncoding[Byte, FeedPrivacyType] (long => FeedPrivacyType.forName(long))
  implicit val FriendRequestStatusTypeDecode: MappedEncoding[FriendRequestStatusType, Byte] = MappedEncoding[FriendRequestStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val FriendRequestStatusTypeEncode: MappedEncoding[Byte, FriendRequestStatusType] = MappedEncoding[Byte, FriendRequestStatusType] (long => FriendRequestStatusType.forName(long))
  implicit val GroupAuthorityTypeDecode: MappedEncoding[GroupAuthorityType, Byte] = MappedEncoding[GroupAuthorityType, Byte] (enumValue => enumValue.toValue)
  implicit val GroupAuthorityTypeEncode: MappedEncoding[Byte, GroupAuthorityType] = MappedEncoding[Byte, GroupAuthorityType] (long => GroupAuthorityType.forName(long))
  implicit val GroupInvitationStatusTypeDecode: MappedEncoding[GroupInvitationStatusType, Byte] = MappedEncoding[GroupInvitationStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val GroupInvitationStatusTypeEncode: MappedEncoding[Byte, GroupInvitationStatusType] = MappedEncoding[Byte, GroupInvitationStatusType] (long => GroupInvitationStatusType.forName(long))
  implicit val GroupPrivacyTypeDecode: MappedEncoding[GroupPrivacyType, Byte] = MappedEncoding[GroupPrivacyType, Byte] (enumValue => enumValue.toValue)
  implicit val GroupPrivacyTypeEncode: MappedEncoding[Byte, GroupPrivacyType] = MappedEncoding[Byte, GroupPrivacyType] (long => GroupPrivacyType.forName(long))
  implicit val MediumTypeDecode: MappedEncoding[MediumType, Byte] = MappedEncoding[MediumType, Byte] (enumValue => enumValue.toValue)
  implicit val MediumTypeEncode: MappedEncoding[Byte, MediumType] = MappedEncoding[Byte, MediumType] (long => MediumType.forName(long))
  implicit val MessageTypeDecode: MappedEncoding[MessageType, Byte] = MappedEncoding[MessageType, Byte] (enumValue => enumValue.toValue)
  implicit val MessageTypeEncode: MappedEncoding[Byte, MessageType] = MappedEncoding[Byte, MessageType] (long => MessageType.forName(long))
  implicit val NotificationTypeDecode: MappedEncoding[NotificationType, Byte] = MappedEncoding[NotificationType, Byte] (enumValue => enumValue.toValue)
  implicit val NotificationTypeEncode: MappedEncoding[Byte, NotificationType] = MappedEncoding[Byte, NotificationType] (long => NotificationType.forName(long))
  implicit val PermissionTypeDecode: MappedEncoding[PermissionType, String] = MappedEncoding[PermissionType, String] (enumValue => enumValue.toValue)
  implicit val PermissionTypeEncode: MappedEncoding[String, PermissionType] = MappedEncoding[String, PermissionType] (string => PermissionType.forName(string))
  implicit val PushNotificationTypeDecode: MappedEncoding[PushNotificationType, Byte] = MappedEncoding[PushNotificationType, Byte] (enumValue => enumValue.toValue)
  implicit val PushNotificationTypeEncode: MappedEncoding[Byte, PushNotificationType] = MappedEncoding[Byte, PushNotificationType] (long => PushNotificationType.forName(long))
  implicit val ReportTypeDecode: MappedEncoding[ReportType, Byte] = MappedEncoding[ReportType, Byte] (enumValue => enumValue.toValue)
  implicit val ReportTypeEncode: MappedEncoding[Byte, ReportType] = MappedEncoding[Byte, ReportType] (long => ReportType.forName(long))
  implicit val ContentStatusTypeDecode: MappedEncoding[ContentStatusType, Byte] = MappedEncoding[ContentStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val ContentStatusTypeEncode: MappedEncoding[Byte, ContentStatusType] = MappedEncoding[Byte, ContentStatusType] (long => ContentStatusType.forName(long))
  implicit val DeviceTypeDecode: MappedEncoding[DeviceType, Byte] = MappedEncoding[DeviceType, Byte] (enumValue => enumValue.toValue)
  implicit val DeviceTypeEncode: MappedEncoding[Byte, DeviceType] = MappedEncoding[Byte, DeviceType] (long => DeviceType.forName(long))

}
