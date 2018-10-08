package io.github.cactacea.backend.core.application.components.services

import com.twitter.inject.domain.WrappedValue
import com.typesafe.config.Config
import io.getquill._

class DatabaseService(config: Config) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config) {

  implicit class IdComparison[T <: WrappedValue[Long]](left: T) {
    def >(right: Long) = quote(infix"$left > $right".as[Boolean])
    def <(right: Long) = quote(infix"$left < $right".as[Boolean])
    def >=(right: Long) = quote(infix"$left >= $right".as[Boolean])
    def <=(right: Long) = quote(infix"$left <= $right".as[Boolean])
  }

}
