import Dependencies.mysql
import _root_.io.github.davidmweber.FlywayPlugin.autoImport._
import sbt.Keys.{libraryDependencies, test, testOnly, testQuick}
import sbt.{Def, _}

object Migration {

  lazy val user = sys.env.get("CACTACEA_MASTER_DB_USERNAME").getOrElse("cactacea")
  lazy val password = sys.env.get("CACTACEA_MASTER_DB_PASSWORD").getOrElse("cactacea")
  lazy val databaseName = sys.env.get("CACTACEA_MASTER_DB_NAME").getOrElse("cactacea")
  lazy val hostName = sys.env.get("CACTACEA_MASTER_DB_HOSTNAME").getOrElse("localhost")
  lazy val port = sys.env.get("CACTACEA_MASTER_DB_PORT").getOrElse("3306")
  lazy val ssl = sys.env.get("CACTACEA_MASTER_DB_SSL").getOrElse("false")

  lazy val settings = Seq(
    flywayUser := user,
    flywayPassword := password,
    flywayUrl := s"jdbc:mysql://${hostName}:${port}/${databaseName}?useSSL=${ssl}",
    flywayPlaceholders := Map("schema" -> databaseName),
    flywayLocations := Seq("filesystem:core/src/main/resources/db/migration/cactacea"),
    (test in Test) := {
      (test in Test).dependsOn(Def.sequential(flywayClean, flywayMigrate)).value
    },
    (testOnly in Test) := {
      (testOnly in Test).dependsOn(Def.sequential(flywayClean, flywayMigrate)).evaluated
    },
    (testQuick in Test) := {
      (testQuick in Test).dependsOn(Def.sequential(flywayClean, flywayMigrate)).evaluated
    },
    libraryDependencies ++= Seq(
      mysql.connector
    )
  )
}
