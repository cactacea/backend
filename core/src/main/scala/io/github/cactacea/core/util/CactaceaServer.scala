package io.github.cactacea.core.util

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.core.application.controllers._
import io.github.cactacea.core.util.filters._
import io.github.cactacea.core.util.warmup.DatabaseWarmupHandler
import io.github.cactacea.core.util.mappers.{CaseClassExceptionMapper, ValidationExceptionMapper}
import io.github.cactacea.core.util.modules.{CacheServiceProviderModule, PushNotificationServiceProviderModule, QueueServiceProviderModule, StorageServiceProviderModule}
import io.github.cactacea.core.util.provider.module.DatabaseProviderModule

class CactaceaServer extends HttpServer {

  override val disableAdminHttpServer = false
  override val defaultFinatraHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "CactaceaServer"

  override val modules = Seq(
    CacheServiceProviderModule,
    DatabaseProviderModule,
    PushNotificationServiceProviderModule,
    QueueServiceProviderModule,
    StorageServiceProviderModule
  )

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[ValidationExceptionMapper]
      .add[AuthFilter, OAuthFilter, ETagFilter, CorsFilter, GroupsController]
      .add[AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MessagesController]
      .add[AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MediumsController]
      .add[AuthFilter, OAuthFilter, ETagFilter, CorsFilter, TimelineController]
      .add[AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FeedsController]
      .add[AuthFilter, OAuthFilter, ETagFilter, CorsFilter, AccountsController]
      .add[AuthFilter, OAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[AuthFilter, ETagFilter, CorsFilter, SettingController]
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

