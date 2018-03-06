package io.github.cactacea.core.infrastructure.services

import java.sql.Timestamp
import java.util.Date

import com.typesafe.config.Config
import io.getquill._
import io.github.cactacea.core.domain.enums._
import org.joda.time.DateTime

class DatabaseService(config: Config) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config) {

  // java objects
  implicit val DatetimeDecode = MappedEncoding[Date, DateTime] (date => new DateTime(date.getTime()))
  implicit val DatetimeEncode = MappedEncoding[DateTime, Date] (dateTime => dateTime.toDate)

  implicit val AccountStatusDecode = MappedEncoding[AccountStatusType, Long] (enumValue => enumValue.toValue)
  implicit val AccountStatusEncode = MappedEncoding[Long, AccountStatusType] (long => AccountStatusType.forName(long))
  implicit val FeedPrivacyTypeDecode = MappedEncoding[FeedPrivacyType, Long] (enumValue => enumValue.toValue)
  implicit val FeedPrivacyTypeEncode = MappedEncoding[Long, FeedPrivacyType] (long => FeedPrivacyType.forName(long))
  implicit val FriendRequestStatusTypeDecode = MappedEncoding[FriendRequestStatusType, Long] (enumValue => enumValue.toValue)
  implicit val FriendRequestStatusTypeEncode = MappedEncoding[Long, FriendRequestStatusType] (long => FriendRequestStatusType.forName(long))
  implicit val GroupAuthorityTypeDecode = MappedEncoding[GroupAuthorityType, Long] (enumValue => enumValue.toValue)
  implicit val GroupAuthorityTypeEncode = MappedEncoding[Long, GroupAuthorityType] (long => GroupAuthorityType.forName(long))
  implicit val GroupInvitationStatusTypeDecode = MappedEncoding[GroupInvitationStatusType, Long] (enumValue => enumValue.toValue)
  implicit val GroupInvitationStatusTypeEncode = MappedEncoding[Long, GroupInvitationStatusType] (long => GroupInvitationStatusType.forName(long))
  implicit val GroupPrivacyTypeDecode = MappedEncoding[GroupPrivacyType, Long] (enumValue => enumValue.toValue)
  implicit val GroupPrivacyTypeEncode = MappedEncoding[Long, GroupPrivacyType] (long => GroupPrivacyType.forName(long))
  implicit val MediumTypeDecode = MappedEncoding[MediumType, Long] (enumValue => enumValue.toValue)
  implicit val MediumTypeEncode = MappedEncoding[Long, MediumType] (long => MediumType.forName(long))
  implicit val MessageTypeDecode = MappedEncoding[MessageType, Long] (enumValue => enumValue.toValue)
  implicit val MessageTypeEncode = MappedEncoding[Long, MessageType] (long => MessageType.forName(long))
  implicit val NotificationTypeDecode = MappedEncoding[NotificationType, Long] (enumValue => enumValue.toValue)
  implicit val NotificationTypeEncode = MappedEncoding[Long, NotificationType] (long => NotificationType.forName(long))
  implicit val PermissionTypeDecode = MappedEncoding[PermissionType, String] (enumValue => enumValue.toValue)
  implicit val PermissionTypeEncode = MappedEncoding[String, PermissionType] (string => PermissionType.forName(string))
  implicit val PushNotificationTypeDecode = MappedEncoding[PushNotificationType, Long] (enumValue => enumValue.toValue)
  implicit val PushNotificationTypeEncode = MappedEncoding[Long, PushNotificationType] (long => PushNotificationType.forName(long))
  implicit val ReportTypeDecode = MappedEncoding[ReportType, Long] (enumValue => enumValue.toValue)
  implicit val ReportTypeEncode = MappedEncoding[Long, ReportType] (long => ReportType.forName(long))

  implicit class TimestampQuotes(left: Timestamp) {
    def >(right: Timestamp) = quote(infix"$left > $right".as[Boolean])
    def <(right: Timestamp) = quote(infix"$left < $right".as[Boolean])
  }

}
