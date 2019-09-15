package io.github.cactacea.backend.core.helpers.specs

import com.twitter.inject.app.TestInjector
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.helpers.generators.{ModelsGenerator, StatusGenerator}

class ServiceSpec extends Spec
  with StatusGenerator
  with ModelsGenerator {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
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
//  val tweetsService = injector.instance[TweetsService]
//
//  def signUp(userName: String, password: String, udid: String) = {
//    execute(usersRepository.create(userName)) //, udid, DeviceType.ios, Some("user-agent")))
//  }


}
