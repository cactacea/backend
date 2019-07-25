package io.github.cactacea.backend.core.helpers

import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.logging.Logging
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.AccountsRepository
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.backend.core.util.modules.CoreModule
import io.github.cactacea.backend.core.helpers.HelperDAO
import org.scalatest.BeforeAndAfter

class RepositorySpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        CoreModule,
        DefaultChatModule,
        DefaultMessageModule,
        DefaultQueueModule,
        DefaultMobilePushModule,
        DefaultStorageModule,
        DefaultDeepLinkModule,
        DefaultJacksonModule
      )
    ).create

  def execute[T](f: => Future[T]): T = {
    Await.result(db.transaction(f))
  }

  val db = injector.instance[DatabaseService]
  val accountsRepository = injector.instance[AccountsRepository]
  val devicesDAO = injector.instance[DevicesDAO]
  val helperDAO = injector.instance[HelperDAO]

  def signUp(accountName: String, password: String, udid: String): Account = {

    val a = execute(accountsRepository.create(accountName))
    execute(devicesDAO.create(udid, DeviceType.ios, Some("user agent"), a.id.toSessionId))
    a
  }

//  def signIn(accountName: String, password: String, udid: String): Account = {
//
//      val result = execute(accountsRepository.find(accountName, password, udid, DeviceType.ios, Some("user agent")))
////    val authentication = execute(devicesDAO.update(accountName, udid, DeviceType.ios, Some("user agent")))
////    authentication
//
//  }

}

