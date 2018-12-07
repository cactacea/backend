package io.github.cactacea.backend

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

//  def setupImages(localPath: String): Unit = {
//
//    val uri = getClass.getResource(localPath).toURI
//    var myPath: Path = null
//
//    if (uri.getScheme == "jar") {
//      val fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap[String, Object]())
//      myPath = fileSystem.getPath("/regression/");
//    } else {
//      myPath = Paths.get(uri);
//    }
//
//    val walk = Files.walk(myPath, 1)
//    val it = walk.sorted().iterator()
//    it.next()//最初の一つ目はregressionフォルダ自体なので飛ばす
//    while (it.hasNext) {
//      val path = it.next()
//      println(path.toAbsolutePath.toString)
//    }
//
//  }
//
//  private def copyFile(file: Path, localPath: String): Unit = {
//    val origin = Paths.get(file.toUri) // Paths.get(this.getClass.getClassLoader.getResource(s"demo/images/$resourceName").toURI)
//    val tmp = Paths.get(s"$localPath/${file.getFileName}")
//    Files.deleteIfExists(tmp)
//    Files.copy(origin, tmp)
//  }

}
