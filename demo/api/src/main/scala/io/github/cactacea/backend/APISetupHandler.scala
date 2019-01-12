package io.github.cactacea.backend

import com.google.inject.{Inject, Singleton}
import com.twitter.inject.annotations.Flag
import com.twitter.inject.utils.Handler

@Singleton
class APISetupHandler  @Inject()(@Flag("storage.localPath") localPath: String) extends Handler {

  override def handle(): Unit = {
    APISetup.migrate()
    setupImages()
  }

  def setupImages(): Unit = {
    new FileAccessUtil().copyAll("demo/images", localPath);

  }

}
