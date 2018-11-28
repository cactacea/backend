package io.github.cactacea.backend.core.helpers

import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.logging.Logging
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.{FeedsService, SessionsService, TimeService}
import io.github.cactacea.backend.core.domain.enums.DeviceType
import org.scalatest.BeforeAndAfter

class ServiceSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
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

  private val db = injector.instance[DatabaseService]
  private val sessionService = injector.instance[SessionsService]
  val feedsService = injector.instance[FeedsService]
  val timeService = injector.instance[TimeService]

  def signUp(accountName: String, password: String, udid: String) = {
    execute(sessionService.signUp(accountName, password, "ffc1ded6f4570d557ad65f986684fc10c7f8d51f", Some("user-agent"), DeviceType.ios))
  }


}
