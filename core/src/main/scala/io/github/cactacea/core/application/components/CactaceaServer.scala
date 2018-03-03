package io.github.cactacea.core.application.components

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.core.application.components.modules._
import io.github.cactacea.core.infrastructure.services.DatabaseProviderModule
import io.github.cactacea.core.util.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}

class CactaceaServer extends HttpServer {

  override val disableAdminHttpServer = false
  override val defaultFinatraHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "Backend Server"

  protected  val databaseModule = DatabaseProviderModule

  val actionModule = DefaultActionModule
  val configModule = DefaultConfigModule
  val fanOutModule = DefaultFanOutModule
  val messageModule = DefaultPushNotificationMessagesModule
  val notificationModule = DefaultNotificationModule
  val publishModule = DefaultPublishModule
  val pushNotificationModule = DefaultPushNotificationModule
  val storageModule = DefaultStorageModule
  val subScribeModule = DefaultSubScribeModule
  val transcodeModule =DefaultTranscodeModule

  addFrameworkModules(
    databaseModule,
    actionModule,
    configModule,
    publishModule,
    subScribeModule,
    fanOutModule,
    messageModule,
    notificationModule,
    pushNotificationModule,
    storageModule,
    transcodeModule
  )

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
  }

}

