import Dependencies.mysql
import _root_.io.github.davidmweber.FlywayPlugin.autoImport._
import sbt.Keys.{libraryDependencies, test, testOnly, testQuick}
import sbt.{Def, _}

object Migration {

  val user = sys.env.get("CACTACEA_MASTER_DB_USERNAME").getOrElse("cactacea")
  val password = sys.env.get("CACTACEA_MASTER_DB_PASSWORD").getOrElse("cactacea")
  val databaseName = sys.env.get("CACTACEA_MASTER_DB_NAME").getOrElse("cactacea")
  val hostName = sys.env.get("CACTACEA_MASTER_DB_HOSTNAME").getOrElse("localhost")
  val port = sys.env.get("CACTACEA_MASTER_DB_PORT").getOrElse("3306")
  val options = sys.env.get("CACTACEA_MASTER_DB_OPTIONS").getOrElse("?useSSL=false&allowPublicKeyRetrieval=true")

  val settings = Seq(
    flywayUser := user,
    flywayPassword := password,
    flywayUrl := s"jdbc:mysql://${hostName}:${port}/${databaseName}${options}",
    flywayPlaceholders := Map("schema" -> databaseName),
    flywayLocations := Seq("filesystem:core/src/main/resources/db/migration/cactacea"),
    test := ((test in Test) dependsOn Def.sequential(flywayClean, flywayMigrate)).value,
    testOnly := ((testOnly in Test) dependsOn Def.sequential(flywayClean, flywayMigrate)).evaluated,
    testQuick := ((testQuick in Test) dependsOn Def.sequential(flywayClean, flywayMigrate)).evaluated,
    libraryDependencies ++= mysql
  )
}
