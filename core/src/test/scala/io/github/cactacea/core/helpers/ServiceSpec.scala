package io.github.cactacea.backend.core.helpers

import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.logging.Logging
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.FeedsService
import io.github.cactacea.backend.core.domain.repositories.AccountsRepository
import org.scalatest.BeforeAndAfter

class ServiceSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultListenerModule,
        DefaultChatModule,
        DefaultMessageModule,
        DefaultQueueModule,
        DefaultMobilePushModule,
        DefaultStorageModule,
        DefaultDeepLinkModule,
        DefaultJacksonModule
      )
    ).create

  def execute[T](f: => Future[T]) = {
    Await.result(db.transaction(f))
  }

  private val db = injector.instance[DatabaseService]
  private val accountsRepository = injector.instance[AccountsRepository]
  val feedsService = injector.instance[FeedsService]

  def signUp(accountName: String, password: String, udid: String) = {
    execute(accountsRepository.create(accountName)) //, udid, DeviceType.ios, Some("user-agent")))
  }


}
