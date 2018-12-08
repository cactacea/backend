package io.github.cactacea.backend



import com.google.inject.{Inject, Singleton}
import com.twitter.inject.annotations.Flag
import com.twitter.inject.utils.Handler


@Singleton
class DemoSetupHandler  @Inject()(@Flag("storage.localPath") localPath: String) extends Handler {

  override def handle(): Unit = {
    DemoSetup.migrate()
    setupImages()
  }

  def setupImages(): Unit = {
    import java.nio.file.{Files, Paths}

    val uri = getClass.getResource("/demo/images").toURI
    val resourcePath = Paths.get(uri)
    val to = Paths.get(localPath)
    if (!Files.exists(to)) {
      Files.createDirectories(to)
    }
    val walk = Files.walk(resourcePath, 1)
    val it = walk.sorted().iterator()
    it.next()
    while (it.hasNext) {
      val path = it.next()
      val toFile = to.resolve(path.getFileName)
      Files.deleteIfExists(toFile)
      Files.copy(path, toFile)
    }

  }

}
