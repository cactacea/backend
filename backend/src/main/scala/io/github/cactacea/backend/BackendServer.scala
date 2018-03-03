package io.github.cactacea.backend

import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.controllers._
import io.github.cactacea.core.application.components.CactaceaServer
import io.github.cactacea.core.util.filters._
import io.github.cactacea.core.util.handlers.DatabaseWarmupHandler

class BackendServer extends CactaceaServer {

  override def configureHttp(router: HttpRouter) = {
    router
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, GroupsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MessagesController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MediumsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, TimelineController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FeedsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, AccountsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, SettingController]
      .add[ApplicationFilter, CorsFilter, SessionsController]
      .add[CorsFilter, OAuthController]
      .add[ResourcesController]
      .add[HealthController]
  }

  override def warmup() {
    handle[DatabaseWarmupHandler]()
  }

}

