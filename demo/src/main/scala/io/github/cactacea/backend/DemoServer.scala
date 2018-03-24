package io.github.cactacea.backend
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.components.authentications.GeneralSocialAccountsModule
import io.github.cactacea.backend.components.storages.s3.S3ServiceModule

class DemoServer extends BackendServer {

  override def storageModule: TwitterModule = S3ServiceModule
  override def socialAccountsModule: TwitterModule = GeneralSocialAccountsModule

}

