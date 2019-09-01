package io.github.cactacea.backend.core.helpers.specs

import com.twitter.inject.app.TestInjector
import com.twitter.util.logging.Logging
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.helpers.generators.ModelsGenerator
import io.github.cactacea.backend.core.helpers.tests.IntegrationFeatureTest
import io.github.cactacea.backend.core.util.modules.CoreModule
import org.scalatest.BeforeAndAfter

class ServiceSpec extends IntegrationFeatureTest
  with ModelsGenerator
  with BeforeAndAfter
  with Logging {

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

//  def execute[T](f: => Future[T]) = {
//    Await.result(db.transaction(f))
//  }
//
//  private val db = injector.instance[DatabaseService]
//  private val usersRepository = injector.instance[UsersRepository]
//  val feedsService = injector.instance[FeedsService]
//
//  def signUp(userName: String, password: String, udid: String) = {
//    execute(usersRepository.create(userName)) //, udid, DeviceType.ios, Some("user-agent")))
//  }


}
