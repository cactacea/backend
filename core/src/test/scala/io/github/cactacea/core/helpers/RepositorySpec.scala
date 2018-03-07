package io.github.cactacea.core.helpers

import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import com.twitter.util.logging.Logging
import io.github.cactacea.core.application.components.modules._
import io.github.cactacea.core.domain.repositories.SessionsRepository
import io.github.cactacea.core.infrastructure.services.{DatabaseProviderModule, DatabaseService}
import org.scalatest.BeforeAndAfter

class RepositorySpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultSocialAccountsModule,
        DefaultInjectionModule,
        DefaultConfigModule,
        DefaultFanOutModule,
        DefaultNotificationMessagesModule,
        DefaultPublishModule,
        DefaultPushNotificationModule,
        DefaultStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        DefaultIdentifyModule,
        FinatraJacksonModule
      )
    ).create

  before {
    DatabaseHelper.initialize()
  }

  after {
    DatabaseHelper.initialize()
  }

  val db = injector.instance[DatabaseService]

  def signUp(accountName: String, password: String, udid: String) = {

    val sessionsRepository = injector.instance[SessionsRepository]
    Await.result(sessionsRepository.signUp(accountName, accountName, password, udid,
      Some("test@example.com"),
      None,
      Some("location"),
      Some("bio"),
      "user agent"))
    val authentication = Await.result(sessionsRepository.signIn(accountName, password, udid, "user agent"))
    authentication
  }

  def signIn(displayName: String, password: String, udid: String) = {

    val sessionsRepository = injector.instance[SessionsRepository]
    val result = Await.result(sessionsRepository.signIn(displayName, password, udid, "user agent"))
    val authentication = Await.result(sessionsRepository.signIn(result.account.displayName, password, udid, "user agent"))
    authentication

  }
}
