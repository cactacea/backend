package io.github.cactacea.core.components

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.core.components.modules.{DefaultPushNotificationModule, DefaultQueueModule, DefaultStorageModule}
import io.github.cactacea.core.infrastructure.services.DatabaseProviderModule
import io.github.cactacea.core.util.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}

class DefaultServer extends HttpServer {

  override val disableAdminHttpServer = false
  override val defaultFinatraHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "Backend Server"

  protected  val databaseModule = DatabaseProviderModule

  val pushNotificationModule = DefaultPushNotificationModule
  val queueModule = DefaultQueueModule
  val storageModule = DefaultStorageModule

  addFrameworkModules(
    databaseModule,
    pushNotificationModule,
    queueModule,
    storageModule
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

