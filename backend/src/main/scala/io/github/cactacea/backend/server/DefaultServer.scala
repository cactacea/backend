package io.github.cactacea.backend.server

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.handlers.DatabaseWarmupHandler
import io.github.cactacea.backend.modules._
import io.github.cactacea.core.application.controllers._
import io.github.cactacea.core.infrastructure.services.DatabaseProviderModule
import io.github.cactacea.core.util.filters._
import io.github.cactacea.core.util.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}

class DefaultServer extends HttpServer {

  override val disableAdminHttpServer = false
  override val defaultFinatraHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "CactaceaServer"

  override val modules = Seq(
    DatabaseProviderModule,
    DefaultPushNotificationModule,
    DefaultQueueModule,
    DefaultStorageModule
  )

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, GroupsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MessagesController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MediumsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, TimelineController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FeedsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, AccountsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, SettingController]
      .add[ApplicationFilter, CorsFilter, SessionsController]
      .add[ApplicationFilter, WorkerTierController]
      .add[CorsFilter, OAuthController]
      .add[ResourcesController]
      .add[HealthController]
  }

  override def warmup() {
    handle[DatabaseWarmupHandler]()
  }

}

