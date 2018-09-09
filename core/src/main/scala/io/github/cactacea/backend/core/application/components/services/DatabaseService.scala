package io.github.cactacea.backend.core.application.components.services

import java.util.Date

import com.twitter.inject.domain.WrappedValue
import com.typesafe.config.Config
import io.getquill._
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import org.joda.time.DateTime

class DatabaseService(config: Config) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config) {

  // java objects
  implicit val datetimeDecode: MappedEncoding[Date, DateTime] = MappedEncoding[Date, DateTime] (date => new DateTime(date.getTime()))
  implicit val datetimeEncode: MappedEncoding[DateTime, Date] = MappedEncoding[DateTime, Date] (dateTime => dateTime.toDate)

  implicit val accountIdDecode: MappedEncoding[AccountId, Long] = MappedEncoding[AccountId, Long] (id => id.onlyValue)
  implicit val accountIdEncode: MappedEncoding[Long, AccountId] = MappedEncoding[Long, AccountId] (long => AccountId(long))
  implicit val accountGroupIdDecode: MappedEncoding[AccountGroupId, Long] = MappedEncoding[AccountGroupId, Long] (id => id.onlyValue)
  implicit val accountGroupIdEncode: MappedEncoding[Long, AccountGroupId] = MappedEncoding[Long, AccountGroupId] (long => AccountGroupId(long))
  implicit val accountReportIdDecode: MappedEncoding[AccountReportId, Long] = MappedEncoding[AccountReportId, Long] (id => id.onlyValue)
  implicit val accountReportIdEncode: MappedEncoding[Long, AccountReportId] = MappedEncoding[Long, AccountReportId] (long => AccountReportId(long))
  implicit val blockIdDecode: MappedEncoding[BlockId, Long] = MappedEncoding[BlockId, Long] (id => id.onlyValue)
  implicit val blockIdEncode: MappedEncoding[Long, BlockId] = MappedEncoding[Long, BlockId] (long => BlockId(long))
  implicit val commentIdDecode: MappedEncoding[CommentId, Long] = MappedEncoding[CommentId, Long] (id => id.onlyValue)
  implicit val commentIdEncode: MappedEncoding[Long, CommentId] = MappedEncoding[Long, CommentId] (long => CommentId(long))
  implicit val commentLikeIdDecode: MappedEncoding[CommentLikeId, Long] = MappedEncoding[CommentLikeId, Long] (id => id.onlyValue)
  implicit val commentLikeIdEncode: MappedEncoding[Long, CommentLikeId] = MappedEncoding[Long, CommentLikeId] (long => CommentLikeId(long))
  implicit val commentReportIdDecode: MappedEncoding[CommentReportId, Long] = MappedEncoding[CommentReportId, Long] (id => id.onlyValue)
  implicit val commentReportIdEncode: MappedEncoding[Long, CommentReportId] = MappedEncoding[Long, CommentReportId] (long => CommentReportId(long))
  implicit val deviceIdDecode: MappedEncoding[DeviceId, Long] = MappedEncoding[DeviceId, Long] (id => id.onlyValue)
  implicit val deviceIdEncode: MappedEncoding[Long, DeviceId] = MappedEncoding[Long, DeviceId] (long => DeviceId(long))
  implicit val feedIdDecode: MappedEncoding[FeedId, Long] = MappedEncoding[FeedId, Long] (id => id.onlyValue)
  implicit val feedIdEncode: MappedEncoding[Long, FeedId] = MappedEncoding[Long, FeedId] (long => FeedId(long))
  implicit val feedLikeIdDecode: MappedEncoding[FeedLikeId, Long] = MappedEncoding[FeedLikeId, Long] (id => id.onlyValue)
  implicit val feedLikeIdEncode: MappedEncoding[Long, FeedLikeId] = MappedEncoding[Long, FeedLikeId] (long => FeedLikeId(long))
  implicit val feedReportIdDecode: MappedEncoding[FeedReportId, Long] = MappedEncoding[FeedReportId, Long] (id => id.onlyValue)
  implicit val feedReportIdEncode: MappedEncoding[Long, FeedReportId] = MappedEncoding[Long, FeedReportId] (long => FeedReportId(long))
  implicit val friendRequestIdDecode: MappedEncoding[FriendRequestId, Long] = MappedEncoding[FriendRequestId, Long] (id => id.onlyValue)
  implicit val friendRequestIdEncode: MappedEncoding[Long, FriendRequestId] = MappedEncoding[Long, FriendRequestId] (long => FriendRequestId(long))
  implicit val groupIdDecode: MappedEncoding[GroupId, Long] = MappedEncoding[GroupId, Long] (id => id.onlyValue)
  implicit val groupIdEncode: MappedEncoding[Long, GroupId] = MappedEncoding[Long, GroupId] (long => GroupId(long))
  implicit val groupInvitationIdDecode: MappedEncoding[GroupInvitationId, Long] = MappedEncoding[GroupInvitationId, Long] (id => id.onlyValue)
  implicit val groupInvitationIdEncode: MappedEncoding[Long, GroupInvitationId] = MappedEncoding[Long, GroupInvitationId] (long => GroupInvitationId(long))
  implicit val groupReportIdDecode: MappedEncoding[GroupReportId, Long] = MappedEncoding[GroupReportId, Long] (id => id.onlyValue)
  implicit val groupReportIdEncode: MappedEncoding[Long, GroupReportId] = MappedEncoding[Long, GroupReportId] (long => GroupReportId(long))
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

  implicit val accountStatusDecode: MappedEncoding[AccountStatusType, Byte] = MappedEncoding[AccountStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val accountStatusEncode: MappedEncoding[Byte, AccountStatusType] = MappedEncoding[Byte, AccountStatusType] (long => AccountStatusType.forName(long))
  implicit val feedPrivacyTypeDecode: MappedEncoding[FeedPrivacyType, Byte] = MappedEncoding[FeedPrivacyType, Byte] (enumValue => enumValue.toValue)
  implicit val feedPrivacyTypeEncode: MappedEncoding[Byte, FeedPrivacyType] = MappedEncoding[Byte, FeedPrivacyType] (long => FeedPrivacyType.forName(long))
  implicit val friendRequestStatusTypeDecode: MappedEncoding[FriendRequestStatusType, Byte] = MappedEncoding[FriendRequestStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val friendRequestStatusTypeEncode: MappedEncoding[Byte, FriendRequestStatusType] = MappedEncoding[Byte, FriendRequestStatusType] (long => FriendRequestStatusType.forName(long))
  implicit val groupAuthorityTypeDecode: MappedEncoding[GroupAuthorityType, Byte] = MappedEncoding[GroupAuthorityType, Byte] (enumValue => enumValue.toValue)
  implicit val groupAuthorityTypeEncode: MappedEncoding[Byte, GroupAuthorityType] = MappedEncoding[Byte, GroupAuthorityType] (long => GroupAuthorityType.forName(long))
  implicit val groupInvitationStatusTypeDecode: MappedEncoding[GroupInvitationStatusType, Byte] = MappedEncoding[GroupInvitationStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val groupInvitationStatusTypeEncode: MappedEncoding[Byte, GroupInvitationStatusType] = MappedEncoding[Byte, GroupInvitationStatusType] (long => GroupInvitationStatusType.forName(long))
  implicit val groupPrivacyTypeDecode: MappedEncoding[GroupPrivacyType, Byte] = MappedEncoding[GroupPrivacyType, Byte] (enumValue => enumValue.toValue)
  implicit val groupPrivacyTypeEncode: MappedEncoding[Byte, GroupPrivacyType] = MappedEncoding[Byte, GroupPrivacyType] (long => GroupPrivacyType.forName(long))
  implicit val mediumTypeDecode: MappedEncoding[MediumType, Byte] = MappedEncoding[MediumType, Byte] (enumValue => enumValue.toValue)
  implicit val mediumTypeEncode: MappedEncoding[Byte, MediumType] = MappedEncoding[Byte, MediumType] (long => MediumType.forName(long))
  implicit val messageTypeDecode: MappedEncoding[MessageType, Byte] = MappedEncoding[MessageType, Byte] (enumValue => enumValue.toValue)
  implicit val messageTypeEncode: MappedEncoding[Byte, MessageType] = MappedEncoding[Byte, MessageType] (long => MessageType.forName(long))
  implicit val notificationTypeDecode: MappedEncoding[NotificationType, Byte] = MappedEncoding[NotificationType, Byte] (enumValue => enumValue.toValue)
  implicit val notificationTypeEncode: MappedEncoding[Byte, NotificationType] = MappedEncoding[Byte, NotificationType] (long => NotificationType.forName(long))
  implicit val pushNotificationTypeDecode: MappedEncoding[PushNotificationType, Byte] = MappedEncoding[PushNotificationType, Byte] (enumValue => enumValue.toValue)
  implicit val pushNotificationTypeEncode: MappedEncoding[Byte, PushNotificationType] = MappedEncoding[Byte, PushNotificationType] (long => PushNotificationType.forName(long))
  implicit val reportTypeDecode: MappedEncoding[ReportType, Byte] = MappedEncoding[ReportType, Byte] (enumValue => enumValue.toValue)
  implicit val reportTypeEncode: MappedEncoding[Byte, ReportType] = MappedEncoding[Byte, ReportType] (long => ReportType.forName(long))
  implicit val contentStatusTypeDecode: MappedEncoding[ContentStatusType, Byte] = MappedEncoding[ContentStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val contentStatusTypeEncode: MappedEncoding[Byte, ContentStatusType] = MappedEncoding[Byte, ContentStatusType] (long => ContentStatusType.forName(long))
  implicit val deviceTypeDecode: MappedEncoding[DeviceType, Byte] = MappedEncoding[DeviceType, Byte] (enumValue => enumValue.toValue)
  implicit val deviceTypeEncode: MappedEncoding[Byte, DeviceType] = MappedEncoding[Byte, DeviceType] (long => DeviceType.forName(long))
  implicit val activeStatusTypeDecode: MappedEncoding[ActiveStatus, Byte] = MappedEncoding[ActiveStatus, Byte] (enumValue => enumValue.toValue)
  implicit val activeStatusTypeEncode: MappedEncoding[Byte, ActiveStatus] = MappedEncoding[Byte, ActiveStatus] (long => ActiveStatus.forName(long))

  implicit class IdComparison[T <: WrappedValue[Long]](left: T) {
    def >(right: Long) = quote(infix"$left > $right".as[Boolean])
    def <(right: Long) = quote(infix"$left < $right".as[Boolean])
    def >=(right: Long) = quote(infix"$left >= $right".as[Boolean])
    def <=(right: Long) = quote(infix"$left <= $right".as[Boolean])
  }

}
