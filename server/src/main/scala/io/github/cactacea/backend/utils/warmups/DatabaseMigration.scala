package io.github.cactacea.backend.utils.warmups

import io.github.cactacea.backend.core.util.configs.Config
import org.flywaydb.core.Flyway

import scala.collection.JavaConverters._

object DatabaseMigration {

  def execute() = {
    val database = Config.db.master.database
    val user = Config.db.master.user
    val password = Config.db.master.password
    val dest = Config.db.master.dest
    val url = s"jdbc:mysql://$dest/$database"
    val flyway = new Flyway()
    flyway.setDataSource(url, user, password)
    flyway.setLocations("classpath:db/migration/cactacea")
    flyway.setPlaceholders(Map("schema" -> database).asJava)
    flyway.migrate()
  }

}