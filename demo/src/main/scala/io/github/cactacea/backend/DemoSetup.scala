package io.github.cactacea.backend

import scala.collection.JavaConverters._
import org.flywaydb.core.Flyway
import io.github.cactacea.backend.core.util.configs.Config

object DemoSetup {

  def migrate(): Unit = {

    val database = Config.db.master.database
    val user = Config.db.master.user
    val password = Config.db.master.password
    val dest = Config.db.master.dest
    val url = s"jdbc:mysql://$dest/$database"

    val flyway = Flyway.configure()
      .dataSource(url, user, password)
      .locations("classpath:db/migration/cactacea")
      .placeholders(Map("schema" -> database, "hostName" -> (Config.storage.hostName + Config.storage.port)).asJava)
      .load()

    flyway.clean()
    flyway.migrate()

  }

}
