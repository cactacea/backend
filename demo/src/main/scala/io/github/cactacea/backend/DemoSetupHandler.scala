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
    new FileAccessUtil().copyAll("demo/images", localPath);

  }

}
