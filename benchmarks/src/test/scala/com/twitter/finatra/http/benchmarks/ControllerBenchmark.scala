package com.twitter.finatra.http.benchmarks

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{ExceptionMappingFilter, HttpResponseFilter}
import com.twitter.finatra.http.modules._
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.app.TestInjector
import com.twitter.inject.internal.modules.LibraryModule
import com.twitter.inject.{Injector, InjectorModule}
import io.github.cactacea.backend.auth.core.application.components.modules.DefaultMailModule
import io.github.cactacea.backend.auth.server.controllers._
import io.github.cactacea.backend.auth.server.utils.filters.AuthenticationFilter
import io.github.cactacea.backend.auth.server.utils.moduels.DefaultAuthModule
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.util.modules.DefaultCoreModule
import io.github.cactacea.backend.server.controllers._
import io.github.cactacea.backend.server.utils.filters.CactaceaAPIKeyFilter
import io.github.cactacea.backend.server.utils.mappers._
import io.github.cactacea.backend.server.utils.modules.{DefaultAPIPrefixModule, DefaultAuthFilterModule}
import io.github.cactacea.backend.utils.{CorsFilter, ETagFilter}
import org.openjdk.jmh.annotations.{Scope, State}

/**
  * ./sbt 'project benchmarks' 'jmh:run ControllerBenchmark'
  */
@State(Scope.Thread)
abstract class ControllerBenchmark extends StdBenchAnnotations {

  val injector: Injector =
    TestInjector(
      modules =
        Seq(
          new LibraryModule("finatra"),
          ExceptionManagerModule,
          MessageBodyModule,
          MustacheModule,
          DocRootModule,
          NullStatsReceiverModule,
          InjectorModule
      ) ++
        Seq(
          DatabaseModule,
          DefaultAPIPrefixModule,
          DefaultAuthModule,
          DefaultAuthFilterModule,
          DefaultCoreModule,
          DefaultJacksonModule,
          DefaultChatModule,
          DefaultDeepLinkModule,
          DefaultMessageModule,
          DefaultMobilePushModule,
          DefaultQueueModule,
          DefaultStorageModule,
          DefaultMailModule,
        )

    ).create

  val httpRouter: HttpRouter = injector.instance[HttpRouter]

  val httpService: Service[Request, Response] =
    httpRouter
      .filter[HttpResponseFilter[Request]]
      .filter[ExceptionMappingFilter[Request]]
      .exceptionMapper[CactaceaCaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
      .exceptionMapper[InvalidPasswordExceptionMapper]
      .exceptionMapper[IdentityNotFoundExceptionMapper]
      .exceptionMapper[OAuthErrorExceptionMapper]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, UsersController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, BlocksController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, CommentsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, CommentLikesController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, TweetsController]
      .add[CactaceaAPIKeyFilter, ETagFilter, CorsFilter, TweetLikesController]
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
      .services
      .externalService

}



