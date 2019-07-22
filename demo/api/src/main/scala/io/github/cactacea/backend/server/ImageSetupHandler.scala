package io.github.cactacea.backend.server

import com.google.inject.{Inject, Singleton}
import com.twitter.inject.annotations.Flag
import com.twitter.inject.utils.Handler
import io.github.cactacea.backend.FileAccessUtil

@Singleton
class ImageSetupHandler  @Inject()(@Flag("storage.localPath") localPath: String) extends Handler {

  override def handle(): Unit = {
    new FileAccessUtil().copyAll("demo/images", localPath);
  }

}
