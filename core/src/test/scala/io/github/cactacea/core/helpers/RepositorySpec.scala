package io.github.cactacea.backend.core.helpers

import com.google.inject.Inject
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.{Await, Future}
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
        DatabaseModule,
        DefaultHashModule
      )
    ).create

  def execute[T](f: => Future[T]) = {
    Await.result(db.transaction(f))
  }

  val db: DatabaseService = injector.instance[DatabaseService]
  @Inject private var sessionsRepository: SessionsRepository = _

  def signUp(accountName: String, password: String, udid: String) = {

    execute(sessionsRepository.signUp(accountName, None, password, udid, DeviceType.ios,
      Some("test@example.com"),
      None,
      Some("location"),
      Some("bio"),
      Some("user agent")))
    val authentication = execute(sessionsRepository.signIn(accountName, password, udid, DeviceType.ios, Some("user agent")))
    authentication
  }

  def signIn(accountName: String, password: String, udid: String) = {

    val result = execute(sessionsRepository.signIn(accountName, password, udid, DeviceType.ios, Some("user agent")))
    val authentication = execute(sessionsRepository.signIn(result.accountName, password, udid, DeviceType.ios, Some("user agent")))
    authentication

  }
}
