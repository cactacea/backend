package io.github.cactacea.backend

import java.nio.file.{Files, Paths}

import io.github.cactacea.backend.core.util.configs.Config

import scala.collection.JavaConverters._

object DemoSetup {

  def migrate(): Unit = {

    val database = Config.db.master.database
    val user = Config.db.master.user
    val password = Config.db.master.password
    val dest = Config.db.master.dest
    val url = s"jdbc:mysql://$dest/$database"

    import org.flywaydb.core.Flyway
    val flyway = Flyway.configure()
      .dataSource(url, user, password)
      .locations("classpath:db/migration/cactacea")
      .placeholders(Map("schema" -> database).asJava)
      .load()
    flyway.clean()
    flyway.migrate()

  }

  def setupData(localPath: String): Unit = {
    copyFile(localPath, "3d481e5c-a8cf-48b5-9b0b-dd131c55058a")
    copyFile(localPath, "8ca7db4e-e53e-4e03-b02f-a308159badf1")
    copyFile(localPath, "8ed1d124-95a1-429c-9cdd-5fdf870fc2a2")
    copyFile(localPath, "9bd131cd-c576-4c78-ae2e-63e90597a89e")
    copyFile(localPath, "9db4e781-981c-4d5b-a774-f752a0a54cfd")
    copyFile(localPath, "17d1c3eb-09d5-4dc3-8412-4bdcdcaadadd")
    copyFile(localPath, "26d273ea-928f-4dc5-b700-4dace2f29d1c")
    copyFile(localPath, "39fb44d3-5524-4cee-ba4d-50ca0e2a051a")
    copyFile(localPath, "65beecd6-d78b-4805-9026-f96cb064b500")
    copyFile(localPath, "74116607-2a0f-4b3c-b0f4-75dea7331c7d")
    copyFile(localPath, "a226019d-dc00-4e97-b83f-d460e368113d")
    copyFile(localPath, "b545bf2d-f11d-4346-9898-33d7aac627ed")
    copyFile(localPath, "b643b26f-0ba1-46b3-965e-6c22ca471be1")
    copyFile(localPath, "c87864bb-31e4-40de-b7fd-f2e22801ba4b")
    copyFile(localPath, "e77ba2bc-6c2a-49ca-807f-6571159bcf78")
    copyFile(localPath, "e9905655-ed5a-4842-986d-16dcf85b7ba1")
    copyFile(localPath, "f65d141e-f95a-40cd-b4df-ef6a2035d8ab")
    copyFile(localPath, "fc20ae30-2b36-42b3-ad88-868ee73cf8ae")
  }

  private def copyFile(localPath: String, resourceName: String): Unit = {
    val origin = Paths.get(this.getClass.getClassLoader.getResource(s"demo/images/$resourceName").toURI)
    val tmp = Paths.get(s"$localPath/$resourceName")
    Files.deleteIfExists(tmp)
    Files.copy(origin, tmp)
  }

}
