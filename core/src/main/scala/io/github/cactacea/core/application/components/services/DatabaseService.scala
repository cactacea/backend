package io.github.cactacea.core.application.components.services

import java.sql.Timestamp
import java.util.Date

import com.typesafe.config.Config
import io.getquill._
import io.github.cactacea.core.domain.enums._
import org.joda.time.DateTime

class DatabaseService(config: Config) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config) {

  // java objects
  implicit val DatetimeDecode: MappedEncoding[Date, DateTime] = MappedEncoding[Date, DateTime] (date => new DateTime(date.getTime()))
  implicit val DatetimeEncode: MappedEncoding[DateTime, Date] = MappedEncoding[DateTime, Date] (dateTime => dateTime.toDate)

  implicit val AccountStatusDecode: MappedEncoding[AccountStatusType, Long] = MappedEncoding[AccountStatusType, Long] (enumValue => enumValue.toValue)
  implicit val AccountStatusEncode: MappedEncoding[Long, AccountStatusType] = MappedEncoding[Long, AccountStatusType] (long => AccountStatusType.forName(long))
  implicit val FeedPrivacyTypeDecode: MappedEncoding[FeedPrivacyType, Long] = MappedEncoding[FeedPrivacyType, Long] (enumValue => enumValue.toValue)
  implicit val FeedPrivacyTypeEncode: MappedEncoding[Long, FeedPrivacyType] = MappedEncoding[Long, FeedPrivacyType] (long => FeedPrivacyType.forName(long))
  implicit val FriendRequestStatusTypeDecode: MappedEncoding[FriendRequestStatusType, Long] = MappedEncoding[FriendRequestStatusType, Long] (enumValue => enumValue.toValue)
  implicit val FriendRequestStatusTypeEncode: MappedEncoding[Long, FriendRequestStatusType] = MappedEncoding[Long, FriendRequestStatusType] (long => FriendRequestStatusType.forName(long))
  implicit val GroupAuthorityTypeDecode: MappedEncoding[GroupAuthorityType, Long] = MappedEncoding[GroupAuthorityType, Long] (enumValue => enumValue.toValue)
  implicit val GroupAuthorityTypeEncode: MappedEncoding[Long, GroupAuthorityType] = MappedEncoding[Long, GroupAuthorityType] (long => GroupAuthorityType.forName(long))
  implicit val GroupInvitationStatusTypeDecode: MappedEncoding[GroupInvitationStatusType, Long] = MappedEncoding[GroupInvitationStatusType, Long] (enumValue => enumValue.toValue)
  implicit val GroupInvitationStatusTypeEncode: MappedEncoding[Long, GroupInvitationStatusType] = MappedEncoding[Long, GroupInvitationStatusType] (long => GroupInvitationStatusType.forName(long))
  implicit val GroupPrivacyTypeDecode: MappedEncoding[GroupPrivacyType, Long] = MappedEncoding[GroupPrivacyType, Long] (enumValue => enumValue.toValue)
  implicit val GroupPrivacyTypeEncode: MappedEncoding[Long, GroupPrivacyType] = MappedEncoding[Long, GroupPrivacyType] (long => GroupPrivacyType.forName(long))
  implicit val MediumTypeDecode: MappedEncoding[MediumType, Long] = MappedEncoding[MediumType, Long] (enumValue => enumValue.toValue)
  implicit val MediumTypeEncode: MappedEncoding[Long, MediumType] = MappedEncoding[Long, MediumType] (long => MediumType.forName(long))
  implicit val MessageTypeDecode: MappedEncoding[MessageType, Long] = MappedEncoding[MessageType, Long] (enumValue => enumValue.toValue)
  implicit val MessageTypeEncode: MappedEncoding[Long, MessageType] = MappedEncoding[Long, MessageType] (long => MessageType.forName(long))
  implicit val NotificationTypeDecode: MappedEncoding[NotificationType, Long] = MappedEncoding[NotificationType, Long] (enumValue => enumValue.toValue)
  implicit val NotificationTypeEncode: MappedEncoding[Long, NotificationType] = MappedEncoding[Long, NotificationType] (long => NotificationType.forName(long))
  implicit val PermissionTypeDecode: MappedEncoding[PermissionType, String] = MappedEncoding[PermissionType, String] (enumValue => enumValue.toValue)
  implicit val PermissionTypeEncode: MappedEncoding[String, PermissionType] = MappedEncoding[String, PermissionType] (string => PermissionType.forName(string))
  implicit val PushNotificationTypeDecode: MappedEncoding[PushNotificationType, Long] = MappedEncoding[PushNotificationType, Long] (enumValue => enumValue.toValue)
  implicit val PushNotificationTypeEncode: MappedEncoding[Long, PushNotificationType] = MappedEncoding[Long, PushNotificationType] (long => PushNotificationType.forName(long))
  implicit val ReportTypeDecode: MappedEncoding[ReportType, Long] = MappedEncoding[ReportType, Long] (enumValue => enumValue.toValue)
  implicit val ReportTypeEncode: MappedEncoding[Long, ReportType] = MappedEncoding[Long, ReportType] (long => ReportType.forName(long))
  implicit val ContentStatusTypeDecode: MappedEncoding[ContentStatusType, Long] = MappedEncoding[ContentStatusType, Long] (enumValue => enumValue.toValue)
  implicit val ContentStatusTypeEncode: MappedEncoding[Long, ContentStatusType] = MappedEncoding[Long, ContentStatusType] (long => ContentStatusType.forName(long))

  implicit class TimestampQuotes(left: Timestamp) {
    def >(right: Timestamp): Quoted[Boolean] = quote(infix"$left > $right".as[Boolean])
    def <(right: Timestamp): Quoted[Boolean] = quote(infix"$left < $right".as[Boolean])
  }

}
