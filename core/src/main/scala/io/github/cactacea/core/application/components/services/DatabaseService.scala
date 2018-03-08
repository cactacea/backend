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

  implicit class TimestampQuotes(left: Timestamp) {
    def >(right: Timestamp): Quoted[Boolean] = quote(infix"$left > $right".as[Boolean])
    def <(right: Timestamp): Quoted[Boolean] = quote(infix"$left < $right".as[Boolean])
  }

}
