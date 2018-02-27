package io.github.cactacea.core.specs

import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import com.twitter.util.logging.Logging
import io.github.cactacea.core.domain.repositories.SessionRepository
import io.github.cactacea.core.helpers.DatabaseHelper
import io.github.cactacea.core.infrastructure.db.{DatabaseProviderModule, DatabaseService}
import org.scalatest.BeforeAndAfter

class RepositorySpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        FinatraJacksonModule
      )
    ).create

  before {
    DatabaseHelper.initialize()
  }

  val db = injector.instance[DatabaseService]

  def signUp(accountName: String, password: String, udid: String) = {

    val sessionRepository = injector.instance[SessionRepository]
    Await.result(sessionRepository.signUp(accountName, accountName, password, udid,
      Some("test@example.com"),
      None,
      Some("location"),
      Some("bio"),
      "user agent"))
    val authentication = Await.result(sessionRepository.signIn(accountName, password, udid, "user agent"))
    authentication
  }

  def signIn(displayName: String, password: String, udid: String) = {

    val sessionRepository = injector.instance[SessionRepository]
    val result = Await.result(sessionRepository.signIn(displayName, password, udid, "user agent"))
    val authentication = Await.result(sessionRepository.signIn(result.account.displayName, password, udid, "user agent"))
    authentication

  }
}
