package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.auth.core.utils.moduels.DefaultAuthModule
import io.github.cactacea.backend.auth.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.helpers.generators.{Generator, ModelsGenerator, StatusGenerator}
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.modules.DefaultCoreModule
import io.github.cactacea.backend.server.helpers.RequestGenerator
import io.github.cactacea.backend.server.models.requests.session.PostSession
import io.github.cactacea.backend.server.utils.modules.{DefaultAPIPrefixModule, DefaultAuthFilterModule}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

@Singleton
class CactaceaServerSpec extends FeatureTest
  with GeneratorDrivenPropertyChecks
  with Generator
  with StatusGenerator
  with ModelsGenerator
  with RequestGenerator
  with UsersControllerSpec
  with BlocksControllerSpec
  with CommentLikesControllerSpec
  with CommentsControllerSpec
  with FeedLikesControllerSpec
  with FeedsControllerSpec
  with FollowsControllerSpec
  with FriendsControllerSpec
  with ChannelsControllerSpec
  with InvitationsControllerSpec
  with MessagesControllerSpec
  with MutesControllerSpec
  with NotificationsControllerSpec
  with FriendRequestsControllerSpec
  with SessionControllerSpec
  with SessionsControllerSpec
  with SettingsControllerSpec {

  override val server = new EmbeddedHttpServer(
    twitterServer = new CactaceaServer
  )

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultAPIPrefixModule,
        DefaultAuthModule,
        DefaultAuthFilterModule,
        DefaultCoreModule,
        DefaultChatModule,
        DefaultDeepLinkModule,
        DefaultJacksonModule,
        DefaultMessageModule,
        DefaultMobilePushModule,
        DefaultQueueModule,
        DefaultStorageModule,
      )
    ).create

  val mapper: FinatraObjectMapper = injector.instance[FinatraObjectMapper]

  val sessionUserName: String = s"server_test"
  val sessionPassword: String = s"server_test_password_2000"
  val sessionToken: String = createSessionUser()

  def createSessionUser(): String = {

    val signInUpHeaders = Map(Config.auth.headerNames.apiKey -> Config.auth.keys.ios)

    // signUp
    val signUpRequest = RequestBuilder.post(s"/sessions")
    val signUpBody = mapper.writePrettyString(PostSignUp(sessionUserName, sessionPassword, Request()))
    signUpRequest.headers(signInUpHeaders)
    signUpRequest.body(signUpBody)
    server.httpRequest(signUpRequest)

    // signIn
    val signInRequest = RequestBuilder.get(s"/sessions?userName=${sessionUserName}&password=${sessionPassword}")
    signInRequest.headers(signInUpHeaders)
    val signInResponse = server.httpRequest(signInRequest)
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
    server.httpRequest(registerUserRequest)

    token
  }


}
