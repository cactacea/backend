package io.github.cactacea.exmaples.backend

import io.github.cactacea.backend.DefaultServer
import io.github.cactacea.core.application.components.modules._
import io.github.cactacea.core.application.components.thirdparties.s3.S3ServiceModule

class BackendServer extends DefaultServer {

  override def customModules = Seq(
    DefaultSocialAccountsModule,
    DefaultInjectionModule,
    DefaultConfigModule,
    DefaultFanOutModule,
    DefaultNotificationMessagesModule,
    DefaultPublishModule,
    DefaultPushNotificationModule,
    S3ServiceModule,
    DefaultSubScribeModule,
    DefaultTranscodeModule
  )

}

