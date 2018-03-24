package io.github.cactacea.backend.core.application.components.services

import com.twitter.util.Future
import com.typesafe.config.{Config, ConfigFactory}
import io.getquill._
import io.github.cactacea.backend.core.application.components.interfaces.IdentifyService

class DefaultIdentifyService extends IdentifyService {

  private class IdentityDatabaseService(config: Config) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config)

  private val config = ConfigFactory.load("application.conf").getConfig("db.id")
  private val db = new IdentityDatabaseService(config)

  import db._

  override def generate(): Future[Long] = {
    db.transaction {
      val q = quote(infix"""select generateId()""".as[Query[Long]])
      run(q).map({a =>
        a.head
      })
    }
  }

}

