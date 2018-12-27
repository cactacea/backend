package io.github.cactacea.backend.core.util.db

import com.twitter.inject.domain.WrappedValue
import io.getquill.context.sql.SqlContext

trait Operators {
  this: SqlContext[_, _] =>

  implicit class OptionOperators[T1, T2](left: Option[T1]) {
    def eq(right: T2) = {
      quote(infix"$left == $right".as[Boolean])
//      left match {
//        case Some(_) => quote(infix"$left == $right".as[Boolean])
//        case None => quote(infix"$left IS NULL".as[Boolean])
//      }
    }
  }

//  def eq[T](value: Option[T]) = {
//    value match {
//      case Some(value) => quote(infix"$value".as[Boolean])
//      case None => quote(infix"0 = 0".as[Boolean])
//    }
//  }

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
