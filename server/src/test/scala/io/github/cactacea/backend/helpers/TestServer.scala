package io.github.cactacea.backend.helpers


import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.cactacea.finagger.DocsController
import io.github.cactacea.backend.BaseServer
import io.github.cactacea.backend.controllers._
import io.github.cactacea.backend.swagger.CactaceaSwaggerModule
import io.github.cactacea.backend.utils.filters._
import io.github.cactacea.backend.utils.warmups.DatabaseMigrationHandler

class TestServer extends BaseServer {

  flag(name = "cactacea.api.prefix", default = "/", help = "Cactacea Api endpoint prefix")

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
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
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, RequestsController]
      .add[ApplicationFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, SocialAccountSettingsController]
      .add[ApplicationFilter, AuthFilter, ETagFilter, CorsFilter, SettingsController]
      .add[ApplicationFilter, CorsFilter, SocialAccountsController]
      .add[ApplicationFilter, CorsFilter, SessionsController]
      .add[CorsFilter, OAuthController]
      .add[ResourcesController]
      .add[HealthController]
      .add[DocsController]
  }

  addFrameworkModule(CactaceaSwaggerModule)

  override def warmup() {
    handle[DatabaseMigrationHandler]()
  }

}