package io.github.cactacea.core.specs

import com.google.inject.Inject
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import com.twitter.util.logging.Logging
import io.github.cactacea.core.application.services.SessionService
import io.github.cactacea.core.helpers.DatabaseHelper
import io.github.cactacea.core.infrastructure.services.DatabaseProviderModule
import io.github.cactacea.core.mocks.{MockPushNotificationModule, MockQueueModule}
import org.scalatest.BeforeAndAfter

class ServiceSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        MockQueueModule,
        MockPushNotificationModule,
        FinatraJacksonModule
      )
    ).create

  before {
    DatabaseHelper.initialize()
  }

  after {
    DatabaseHelper.initialize()
  }

  @Inject var sessionService: SessionService = _

  def signUp(accountName: String, password: String, udid: String) = {
    Await.result(sessionService.signUp(accountName, accountName, "account password", "ffc1ded6f4570d557ad65f986684fc10c7f8d51f", Some("test@example.com"), None, Some("location"), Some("bio"), "user-agent"))
  }


}
