package io.github.cactacea.core

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.modules._
import io.github.cactacea.core.util.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}
import io.github.cactacea.core.util.warmups.DatabaseWarmupHandler

class CactaceaServer extends HttpServer {

  override val disableAdminHttpServer = false
  override val defaultFinatraHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "Backend Server"

  protected  val databaseModule = DatabaseProviderModule

  def customModules: Seq[TwitterModule] = Seq(
      DefaultIdentifyModule,
      DefaultSocialAccountsModule,
      DefaultInjectionModule,
      DefaultConfigModule,
      DefaultFanOutModule,
      DefaultNotificationMessagesModule,
      DefaultPublishModule,
      DefaultPushNotificationModule,
      DefaultStorageModule,
      DefaultSubScribeModule,
      DefaultTranscodeModule
  )

  addFrameworkModules(customModules:_*)
  addFrameworkModules(databaseModule)

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
  }

  override def warmup() {
    handle[DatabaseWarmupHandler]()
  }

}
