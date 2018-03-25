package io.github.cactacea.backend.core.helpers

import com.google.inject.Inject
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import com.twitter.util.logging.Logging
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.SessionsService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import org.scalatest.BeforeAndAfter

class ServiceSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultSocialAccountsModule,
        DefaultInjectionModule,
        DefaultConfigModule,
        DefaultNotificationModule,
        DefaultNotificationMessagesModule,
        DefaultPublishModule,
        DefaultPushNotificationModule,
        DefaultStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        DefaultIdentifyModule,
        DefaultDeepLinkModule
      )
    ).create

  val db = injector.instance[DatabaseService]

  before {
    DatabaseHelper.initialize(db)
  }

  @Inject private var sessionService: SessionsService = _

  def signUp(accountName: String, password: String, udid: String) = {
    Await.result(sessionService.signUp(accountName, accountName, "account password", "ffc1ded6f4570d557ad65f986684fc10c7f8d51f", Some("test@example.com"), None, Some("location"), Some("bio"), "user-agent", DeviceType.ios))
  }


}
