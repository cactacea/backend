package io.github.cactacea.backend.core.helpers

import com.google.inject.Inject
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.{Await, Future}
import com.twitter.util.logging.Logging
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.{FeedsService, SessionsService, TimeService}
import io.github.cactacea.backend.core.domain.enums.DeviceType
import org.scalatest.BeforeAndAfter

class ServiceSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultSocialAccountsModule,
        DefaultInjectionModule,
        DefaultNotificationModule,
        DefaultNotificationMessagesModule,
        DefaultPublishModule,
        DefaultPushNotificationModule,
        DefaultStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        DefaultDeepLinkModule,
        DefaultHashModule
      )
    ).create

  def execute[T](f: => Future[T]) = {
    Await.result(db.transaction(f))
  }

  @Inject private var db: DatabaseService = _
  @Inject private var sessionService: SessionsService = _
  @Inject var feedsService: FeedsService = _
  @Inject var timeService: TimeService = _

  def signUp(accountName: String, password: String, udid: String) = {
    execute(sessionService.signUp(accountName, None, "account password", "ffc1ded6f4570d557ad65f986684fc10c7f8d51f", Some("test@example.com"), None, Some("location"), Some("bio"), Some("user-agent"), DeviceType.ios))
  }


}
