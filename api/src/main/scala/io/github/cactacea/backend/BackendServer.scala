package io.github.cactacea.backend

import com.jakehschwartz.finatra.swagger.DocsController
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.controllers._
import io.github.cactacea.backend.swagger.{BackendDocumentsController, BackendSwaggerModule}
import io.github.cactacea.backend.util.filters._

class BackendServer extends BaseServer {

  flag(name = "api.prefix", default = "/", help = "Api endpoint prefix")

  override def configureHttp(router: HttpRouter) = {
    super.configureHttp(router)
    router
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, AccountsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, BlocksController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, CommentsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FeedsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FriendsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FollowersController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, GroupsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, InvitationsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MediumsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MessagesController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MutesController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, ReportsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, RequestsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, FacebookSettingController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, GoogleSettingController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, TwitterSettingController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, SettingsController]
      .add[ApplicationFilter, CorsFilter, FacebookController]
      .add[ApplicationFilter, CorsFilter, GoogleController]
      .add[ApplicationFilter, CorsFilter, TwitterController]
      .add[ApplicationFilter, CorsFilter, SessionsController]
      .add[CorsFilter, OAuthController]
      .add[ResourcesController]
      .add[HealthController]
      .add[BackendDocumentsController]
      .add[DocsController]
  }

  addFrameworkModule(BackendSwaggerModule)
}