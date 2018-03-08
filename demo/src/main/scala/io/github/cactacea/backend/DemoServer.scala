package io.github.cactacea.backend

import io.github.cactacea.backend.s3.S3ServiceModule
import io.github.cactacea.core.application.components.modules._

class DemoServer extends DefaultServer {

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
    DefaultTranscodeModule,
    DefaultIdentifyModule
  )

}

