package io.github.cactacea.backend

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.controllers._
import io.github.cactacea.backend.utils.filters._
import io.github.cactacea.backend.utils.oauth.OAuthFilter
import io.github.cactacea.backend.utils.warmups.{CactaceaDatabaseMigrationHandler, QueueHandler}
import io.github.cactacea.utils.{CorsFilter, ETagFilter}

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
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, AccountsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, BlocksController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, CommentsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, CommentLikesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, FeedsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, FeedLikesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, FriendsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, FollowingsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, GroupsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, InvitationsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, MediumsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, MessagesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, MutesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, FriendRequestsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, OAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaTokenFilter, ETagFilter, CorsFilter, SettingsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CorsFilter, SessionsController]
      .add[CorsFilter, AuthController]
      .add[ResourcesController]
      .add[HealthController]
  }

  override def warmup() {
    handle[CactaceaDatabaseMigrationHandler]()
    handle[QueueHandler]()
  }

}
