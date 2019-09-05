package io.github.cactacea.backend.core.application.components.services

import java.util.TimeZone

import com.twitter.finagle.mysql
import io.getquill.{PluralizedTableNames, _}
import io.github.cactacea.backend.core.util.db.{Encoders, Operators}


class DatabaseService(client: OperationType => mysql.Client with mysql.Transactions, timeZone: TimeZone)
  extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), client, timeZone, timeZone)
    with Encoders with Operators {

  def this(master: mysql.Client with mysql.Transactions, slave: mysql.Client with mysql.Transactions, timeZone: TimeZone) = {
    this(_ match {
      case OperationType.Read  => slave
      case OperationType.Write => master
    }, timeZone)
  }

  def this(client: mysql.Client with mysql.Transactions,timeZone: TimeZone) = {
    this(_ => client, timeZone)
  }

}
