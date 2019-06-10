package io.github.cactacea.backend.utils.warmups

import io.github.cactacea.backend.core.util.configs.Config
import scala.collection.JavaConverters._
import org.flywaydb.core.Flyway

object CactaceaDatabaseMigration {

  def execute(): Unit = {
    val database = Config.db.master.database
    val user = Config.db.master.user
    val password = Config.db.master.password
    val dest = Config.db.master.dest
    val options = Config.db.master.options
    val url = s"jdbc:mysql://$dest/$database${options}"


    val flyway = Flyway.configure()
      .dataSource(url, user, password)
      .locations("classpath:db/migration/cactacea")
      .placeholders(Map("schema" -> database).asJava)
      .load()
    flyway.migrate()
  }

}
