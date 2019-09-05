package jp.smartreach.members.backend.core.application.components.services

import java.util.TimeZone

import com.twitter.finagle.mysql
import io.getquill._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import jp.smartreach.members.backend.core.util.db.Encoders


class MembersDatabaseService(client: OperationType => mysql.Client with mysql.Transactions, timeZone: TimeZone)
  extends DatabaseService(client, timeZone) with Encoders {

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
