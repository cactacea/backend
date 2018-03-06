package io.github.cactacea.core.util.warmups

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.routing.HttpWarmup
import com.twitter.inject.utils.Handler
import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

@Singleton
class DatabaseWarmupHandler @Inject()(httpWarmup: HttpWarmup) extends Handler {

  override def handle() = {
    val config = ConfigFactory.load()
    val database = config.getString("db.master.database")
    val user = config.getString("db.master.user")
    val password = config.getString("db.master.password")
    val dest = config.getString("db.master.dest")
    val url = s"jdbc:mysql://$dest/$database"
    val flyway = new Flyway()
    flyway.setDataSource(url, user, password)
    flyway.setBaselineOnMigrate(true)
    flyway.setLocations("filesystem:core/src/main/resources/db/migration")
    flyway.clean()
    flyway.migrate()
  }

}