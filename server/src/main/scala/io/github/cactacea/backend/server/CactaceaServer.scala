package io.github.cactacea.backend.server

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.auth.core.application.components.modules.DefaultMailModule
import io.github.cactacea.backend.auth.server.controllers._
import io.github.cactacea.backend.auth.server.utils.filters.AuthenticationFilter
import io.github.cactacea.backend.auth.server.utils.moduels.DefaultAuthModule
import io.github.cactacea.backend.server.controllers._
import io.github.cactacea.backend.server.utils.filters.CactaceaAPIKeyFilter
import io.github.cactacea.backend.server.utils.mappers.{IdentityNotFoundExceptionMapper, InvalidPasswordExceptionMapper, OAuthErrorExceptionMapper}
import io.github.cactacea.backend.server.utils.modules.{DefaultAPIPrefixModule, DefaultAuthFilterModule}
import io.github.cactacea.backend.server.utils.warmups.{CactaceaDatabaseMigrationHandler, CactaceaQueueHandler}
import io.github.cactacea.backend.utils.{CorsFilter, ETagFilter}

class CactaceaServer extends BaseServer {

  override val disableAdminHttpServer = true
  override val defaultHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "Backend Server"

  override def configureHttp(router: HttpRouter): Unit = {
    super.configureHttp(router)
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .exceptionMapper[InvalidPasswordExceptionMapper]
      .exceptionMapper[IdentityNotFoundExceptionMapper]
      .exceptionMapper[OAuthErrorExceptionMapper]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, UsersController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, BlocksController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, CommentsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, CommentLikesController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, FeedsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, FeedLikesController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, FriendsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, FollowsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, ChannelsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, InvitationsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, MediumsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, MessagesController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, MutesController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, NotificationsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, FriendRequestsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, SessionController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, SettingsController]
      .add[CactaceaAPIKeyFilter, CorsFilter, AuthenticationsController]
      .add[CactaceaAPIKeyFilter, AuthenticationFilter, CorsFilter, AuthenticationController]
      .add[CactaceaAPIKeyFilter, CorsFilter, PasswordController]
      .add[CorsFilter, SocialAuthenticationsController]
      .add[ResourcesController]
      .add[HealthController]
  }

  addFrameworkModule(DefaultAPIPrefixModule)
  addFrameworkModule(DefaultAuthFilterModule)
  addFrameworkModule(DefaultAuthModule)
  addFrameworkModule(DefaultMailModule)

  override def warmup() {
    handle[CactaceaDatabaseMigrationHandler]()
    handle[CactaceaQueueHandler]()
  }

}
