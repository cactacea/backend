package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import com.twitter.util.Future
import com.typesafe.config.{Config, ConfigFactory}
import io.getquill._
import io.github.cactacea.core.application.components.interfaces.IdentifyService

@Singleton
class DefaultIdentifyService extends IdentifyService {

  class IdentityDatabaseService(config: Config) extends FinagleMysqlContext(NamingStrategy(PluralizedTableNames, SnakeCase, MysqlEscape), config)

  val config = ConfigFactory.load("application.conf").getConfig("db.id")
  val db = new IdentityDatabaseService(config)

  import db._

  override def generate(): Future[Long] = {
    db.transaction {
      val q = quote(infix"""select generateId()""".as[Query[Long]])
      run(q).map(_.head)
    }
  }

}

