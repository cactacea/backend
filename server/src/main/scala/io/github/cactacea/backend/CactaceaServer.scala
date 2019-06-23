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
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, AccountsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, BlocksController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, CommentsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, CommentLikesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, FeedsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, FeedLikesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, FriendsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, FollowingsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, GroupsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, InvitationsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, MediumsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, MessagesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, MutesController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, FriendRequestsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, SessionController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CactaceaAuthFilter, ETagFilter, CorsFilter, SettingsController]
      .add[CactaceaLocaleFilter, CactaceaAPIKeyFilter, CorsFilter, SessionsController]
      .add[ResourcesController]
      .add[HealthController]
  }

  override def warmup() {
    handle[CactaceaDatabaseMigrationHandler]()
    handle[CactaceaQueueHandler]()
  }

}
