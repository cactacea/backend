package io.github.cactacea.backend.core.helpers

import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import com.twitter.util.logging.Logging
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import org.scalatest.BeforeAndAfter

class RepositorySpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultConfigModule,
        DefaultIdentifyModule
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
    Await.result(sessionsRepository.signUp(accountName, accountName, password, udid, DeviceType.ios,
      Some("test@example.com"),
      None,
      Some("location"),
      Some("bio"),
      "user agent"))
    val authentication = Await.result(sessionsRepository.signIn(accountName, password, udid, DeviceType.ios, "user agent"))
    authentication
  }

  def signIn(accountName: String, password: String, udid: String) = {

    val sessionsRepository = injector.instance[SessionsRepository]
    val result = Await.result(sessionsRepository.signIn(accountName, password, udid, DeviceType.ios, "user agent"))
    val authentication = Await.result(sessionsRepository.signIn(result.accountName, password, udid, DeviceType.ios, "user agent"))
    authentication

  }
}
