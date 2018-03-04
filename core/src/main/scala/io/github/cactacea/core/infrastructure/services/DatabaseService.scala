package io.github.cactacea.core.infrastructure.services

import java.sql.Timestamp
import java.util.Date

import io.getquill._
import io.github.cactacea.core.domain.enums.{AccountStatusType, NotificationType}
import org.joda.time.DateTime

class DatabaseService(config: String) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config) {

  // java objects
  implicit val datetimeDecode = MappedEncoding[Date, DateTime] (date => new DateTime(date.getTime()))
  implicit val datetimeEncode = MappedEncoding[DateTime, Date] (dateTime => dateTime.toDate)

  implicit val notificationTypeDecode = MappedEncoding[NotificationType, Long] (notificationType => notificationType.toValue)
  implicit val notificationTypeEncode = MappedEncoding[Long, NotificationType] (long => NotificationType.forName(long))

  implicit val accountStatusDecode = MappedEncoding[AccountStatusType, Long] (accountStatus => accountStatus.toValue)
  implicit val accountStatusEncode = MappedEncoding[Long, AccountStatusType] (long => AccountStatusType.forName(long))

  implicit class TimestampQuotes(left: Timestamp) {
    def >(right: Timestamp) = quote(infix"$left > $right".as[Boolean])
    def <(right: Timestamp) = quote(infix"$left < $right".as[Boolean])
  }

}
