package com.twitter.finatra.http.benchmarks

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{ExceptionMappingFilter, HttpResponseFilter}
import com.twitter.finatra.http.modules._
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.Injector
import com.twitter.inject.app.TestInjector
import com.twitter.inject.internal.modules.LibraryModule
import com.twitter.util.Await
import io.github.cactacea.backend.auth.core.application.components.modules.DefaultMailModule
import io.github.cactacea.backend.auth.core.application.services.AuthenticationService
import io.github.cactacea.backend.auth.core.utils.moduels.JWTAuthenticationModule
import io.github.cactacea.backend.auth.server.controllers.{AuthenticationController, AuthenticationPasswordController, AuthenticationSessionController}
import io.github.cactacea.backend.auth.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.modules.DefaultCoreModule
import io.github.cactacea.backend.server.controllers._
import io.github.cactacea.backend.server.utils.filters.CactaceaAPIKeyFilter
import io.github.cactacea.backend.server.utils.mappers.{IdentityNotFoundExceptionMapper, InvalidPasswordExceptionMapper}
import io.github.cactacea.backend.server.utils.modules.APIPrefixModule
import io.github.cactacea.backend.utils.{CorsFilter, ETagFilter}
import org.openjdk.jmh.annotations.{Scope, State}

/**
  * ./sbt 'project benchmarks' 'jmh:run ControllerBenchmark'
  */
@State(Scope.Thread)
abstract class ControllerBenchmark extends StdBenchAnnotations {

  val injector: Injector =
    TestInjector(
      modules = Seq(
        new LibraryModule("finatra"),
        ExceptionManagerModule,
        MessageBodyModule,
        MustacheModule,
        DocRootModule,
        NullStatsReceiverModule
      ) ++
        Seq(
          DatabaseModule,
          APIPrefixModule,
          JWTAuthenticationModule,
          DefaultCoreModule,
          DefaultJacksonModule,
          DefaultChatModule,
          DefaultDeepLinkModule,
          DefaultMessageModule,
          DefaultMobilePushModule,
          DefaultQueueModule,
          DefaultStorageModule,
          DefaultMailModule
        )

    ).create

  val httpRouter: HttpRouter = injector.instance[HttpRouter]

  val httpService: Service[Request, Response] =
    httpRouter
      .filter[HttpResponseFilter[Request]]
      .filter[ExceptionMappingFilter[Request]]
      .exceptionMapper[InvalidPasswordExceptionMapper]
      .exceptionMapper[IdentityNotFoundExceptionMapper]
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
      .add[CactaceaAPIKeyFilter, CorsFilter, AuthenticationController]
      .add[CactaceaAPIKeyFilter, CorsFilter, AuthenticationPasswordController]
      .add[CactaceaAPIKeyFilter, CorsFilter, AuthenticationSessionController]
      .services
      .externalService

  val mapper: FinatraObjectMapper = injector.instance[FinatraObjectMapper]
  val authenticationService: AuthenticationService = injector.instance[AuthenticationService]

  val sessionUserName = s"benchmark_user"
  val sessionPassword = s"benchmark_password_2000"

  def getHeaders(): Map[String, String] = {
    implicit val signUpRequest = Request()
    val response = Await.result(authenticationService.signIn(sessionUserName, sessionPassword))
    val token = response.headerMap.getOrNull(Config.auth.headerNames.authorizationKey)
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios,
      Config.auth.headerNames.authorizationKey -> token
    )
    headers
  }

  def createSessionUser(): Unit = {
    val signUp = PostSignUp(sessionUserName, sessionPassword, Request())
    val body = mapper.writePrettyString(signUp)
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val request = RequestBuilder.post("/sessions")
    request.body(body)
    request.headers(headers)
    httpService(request)
  }

  def beforeAll(): Unit = {
  }

  beforeAll()
  createSessionUser()

  val headers = getHeaders()

}

