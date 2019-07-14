package io.github.cactacea.backend

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.controllers._
import io.github.cactacea.backend.utils.filters._
import io.github.cactacea.backend.utils.warmups.{CactaceaDatabaseMigrationHandler, CactaceaQueueHandler}
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
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, AccountsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, BlocksController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, CommentsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, CommentLikesController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, FeedsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, FeedLikesController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, FriendsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, FollowsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, GroupsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, InvitationsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, MediumsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, MessagesController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, MutesController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, CactaceaLocaleFilter, ETagFilter, CorsFilter, NotificationsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, FriendRequestsController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, SessionController]
      .add[CactaceaAPIKeyFilter, CactaceaAuthenticationFilter, ETagFilter, CorsFilter, SettingsController]
      .add[CactaceaAPIKeyFilter, CorsFilter, SessionsController]
      .add[ResourcesController]
      .add[HealthController]
  }

  override def warmup() {
    handle[CactaceaDatabaseMigrationHandler]()
    handle[CactaceaQueueHandler]()
  }

}
