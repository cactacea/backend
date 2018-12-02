package io.github.cactacea.backend.core.helpers

import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.logging.Logging
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import org.scalatest.BeforeAndAfter

class RepositorySpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultHashModule
      )
    ).create

  def execute[T](f: => Future[T]): T = {
    Await.result(db.transaction(f))
  }

  val db = injector.instance[DatabaseService]
  val sessionsRepository = injector.instance[SessionsRepository]

  def signUp(accountName: String, password: String, udid: String): Account = {

    execute(sessionsRepository.signUp(accountName, password, udid, DeviceType.ios, Some("user agent")))
    val authentication = execute(sessionsRepository.signIn(accountName, password, udid, DeviceType.ios, Some("user agent")))
    authentication
  }

  def signIn(accountName: String, password: String, udid: String): Account = {

    val result = execute(sessionsRepository.signIn(accountName, password, udid, DeviceType.ios, Some("user agent")))
    val authentication = execute(sessionsRepository.signIn(result.accountName, password, udid, DeviceType.ios, Some("user agent")))
    authentication

  }
}
