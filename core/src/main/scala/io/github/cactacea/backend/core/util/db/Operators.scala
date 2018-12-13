package io.github.cactacea.backend.core.util.db

import com.twitter.inject.domain.WrappedValue
import io.getquill.context.sql.SqlContext

trait Operators {
  this: SqlContext[_, _] =>

  implicit class StringOperators(left: String) {
    def lt(right: String) = quote(infix"STRCMP($left, $right) < 0".as[Boolean]) // scalastyle:ignore
    def lte(right: String) = quote(infix"STRCMP($left, $right) <= 0".as[Boolean]) // scalastyle:ignore
    def gt(right: String) = quote(infix"STRCMP($left, $right) > 0".as[Boolean]) // scalastyle:ignore
    def gte(right: String) = quote(infix"STRCMP($left, $right) >= 0".as[Boolean]) // scalastyle:ignore
  }

  implicit class IdOperators[T <: WrappedValue[Long]](left: T) {
    def >(right: Long) = quote(infix"$left > $right".as[Boolean]) // scalastyle:ignore
    def <(right: Long) = quote(infix"$left < $right".as[Boolean]) // scalastyle:ignore
    def >=(right: Long) = quote(infix"$left >= $right".as[Boolean]) // scalastyle:ignore
    def <=(right: Long) = quote(infix"$left <= $right".as[Boolean]) // scalastyle:ignore
  }


}
