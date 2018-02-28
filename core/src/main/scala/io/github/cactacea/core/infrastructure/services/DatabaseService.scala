package io.github.cactacea.core.infrastructure.services

import java.sql.Timestamp
import java.util.Date

import io.getquill._
import org.joda.time.DateTime

class DatabaseService(config: String) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config) {

  // java objects
  implicit val datetimeDecode: MappedEncoding[Date, DateTime] = MappedEncoding[Date, DateTime] (date => new DateTime(date.getTime()))
  implicit val datetimeEncode: MappedEncoding[DateTime, Date] = MappedEncoding[DateTime, Date] (dateTime => dateTime.toDate)

  implicit class TimestampQuotes(left: Timestamp) {
    def >(right: Timestamp) = quote(infix"$left > $right".as[Boolean])
    def <(right: Timestamp) = quote(infix"$left < $right".as[Boolean])
  }

}
