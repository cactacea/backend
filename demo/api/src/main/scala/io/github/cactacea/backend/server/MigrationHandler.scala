package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.utils.Handler
import io.github.cactacea.backend.core.util.configs.Config
import org.flywaydb.core.Flyway

import scala.collection.JavaConverters._


@Singleton
class MigrationHandler extends Handler {

  override def handle(): Unit = {
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

    flyway.migrate()
  }

}

