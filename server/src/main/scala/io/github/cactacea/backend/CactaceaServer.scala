package io.github.cactacea.backend

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.cactacea.finagger.DocsController
import io.github.cactacea.backend.controllers._
import io.github.cactacea.backend.swagger.CactaceaSwaggerModule
import io.github.cactacea.backend.utils.filters._
import io.github.cactacea.backend.utils.warmups.DatabaseMigrationHandler

class CactaceaServer extends BaseServer {

  flag(name = "cactacea.api.prefix", default = "/", help = "Cactacea Api endpoint prefix")

  override val disableAdminHttpServer = false
  override val defaultHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "Backend Server"

  override def configureHttp(router: HttpRouter): Unit = {
    super.configureHttp(router)
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, AccountsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, BlocksController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, CommentsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, CommentLikesController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FeedsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FriendsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, FollowersController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, GroupsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, InvitationsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MediumsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MessagesController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, MutesController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, RequestsController]
      .add[APIFilter, AuthFilter, OAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[APIFilter, AuthFilter, ETagFilter, CorsFilter, SettingsController]
      .add[APIFilter, CorsFilter, SessionsController]
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
