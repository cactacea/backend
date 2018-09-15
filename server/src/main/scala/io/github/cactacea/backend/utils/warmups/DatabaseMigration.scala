package io.github.cactacea.backend.utils.warmups

import io.github.cactacea.backend.core.util.configs.Config
import org.flywaydb.core.Flyway

import scala.collection.JavaConverters._

object DatabaseMigration {

  def execute() = {
    val database = Config.db.database
    val user = Config.db.user
    val password = Config.db.password
    val dest = Config.db.dest
    val url = s"jdbc:mysql://$dest/$database"
    val flyway = new Flyway()
    flyway.setDataSource(url, user, password)
    flyway.setBaselineOnMigrate(true)
    flyway.setPlaceholders(Map("schema" -> database).asJava)
    flyway.migrate()
  }

}