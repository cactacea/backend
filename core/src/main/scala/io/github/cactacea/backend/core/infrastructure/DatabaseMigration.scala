package io.github.cactacea.backend.core.infrastructure

import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

import scala.collection.JavaConverters._

object DatabaseMigration {

  def execute() = {
    val config = ConfigFactory.load()
    val database = config.getString("db.master.database")
    val user = config.getString("db.master.user")
    val password = config.getString("db.master.password")
    val dest = config.getString("db.master.dest")
    val url = s"jdbc:mysql://$dest/$database"
    val flyway = new Flyway()
    flyway.setDataSource(url, user, password)
    flyway.setBaselineOnMigrate(true)
    flyway.setPlaceholders(Map("schema" -> database).asJava)
    flyway.setLocations("classpath:/db/migration/cactacea/")
    flyway.clean()
    flyway.migrate()
  }

}