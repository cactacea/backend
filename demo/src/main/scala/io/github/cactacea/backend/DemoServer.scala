package io.github.cactacea.backend
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.s3.S3ServiceModule

class DemoServer extends DefaultServer {

  override def storageModule: TwitterModule = S3ServiceModule

}

