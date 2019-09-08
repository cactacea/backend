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
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.auth.core.application.components.modules.DefaultMailModule
import io.github.cactacea.backend.auth.core.application.services.AuthenticationService
import io.github.cactacea.backend.auth.core.utils.moduels.DefaultAuthModule
import io.github.cactacea.backend.auth.server.controllers.{AuthenticationController, AuthenticationPasswordController, AuthenticationSessionController}
import io.github.cactacea.backend.auth.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.modules.DefaultCoreModule
import io.github.cactacea.backend.server.controllers._
import io.github.cactacea.backend.server.models.requests.session.PostSession
import io.github.cactacea.backend.server.utils.filters.CactaceaAPIKeyFilter
import io.github.cactacea.backend.server.utils.mappers.{IdentityNotFoundExceptionMapper, InvalidPasswordExceptionMapper}
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
          NullStatsReceiverModule
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

  val sessionUserName: String = s"server_test"
  val sessionPassword: String = s"server_test_password_2000"
  val sessionHeaders: Map[String, String] = createSessionUser()

  def createSessionUser(): Map[String, String] = {

    val signInUpHeaders = Map(Config.auth.headerNames.apiKey -> Config.auth.keys.ios)

    // signUp
    val signUpRequest = RequestBuilder.post(s"/sessions")
    val signUpBody = mapper.writePrettyString(PostSignUp(sessionUserName, sessionPassword, Request()))
    signUpRequest.headers(signInUpHeaders)
    signUpRequest.body(signUpBody)
    Await.result(httpService(signUpRequest).rescue {
      case _: RuntimeException => Future.Unit
    })

    // signIn
    val signInRequest = RequestBuilder.get(s"/sessions?userName=${sessionUserName}&password=${sessionPassword}")
    signInRequest.headers(signInUpHeaders)
    val signInResponse = Await.result(httpService(signInRequest))
    val token = signInResponse.headerMap.getOrNull(Config.auth.headerNames.authorizationKey)

    // registerUser
    val registerUserRequest = RequestBuilder.post(s"/session")
    val registerUserHeaders = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios,
      Config.auth.headerNames.authorizationKey -> token
    )
    val registerUserBody = mapper.writePrettyString(PostSession(sessionUserName, None))
    registerUserRequest.headers(registerUserHeaders)
    registerUserRequest.body(registerUserBody)
    Await.result(httpService(registerUserRequest).rescue {
      case _: RuntimeException => Future.Unit
    })

    registerUserHeaders
  }

  createSessionUser()

}



