package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.helpers.generators.{Generator, ModelsGenerator, StatusGenerator}
import io.github.cactacea.backend.core.util.modules.CactaceaCoreModule
import io.github.cactacea.backend.server.helpers.RequestGenerator
import io.github.cactacea.backend.server.utils.modules.{CactaceaAPIPrefixModule, CactaceaAuthenticationModule}
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
          CactaceaAuthenticationModule,
          CactaceaAPIPrefixModule,
          CactaceaCoreModule,
          DefaultChatModule,
          DefaultDeepLinkModule,
          DefaultJacksonModule,
          DefaultMessageModule,
          DefaultMobilePushModule,
          DefaultQueueModule,
          DefaultStorageModule,
      )
    ).create

  val mapper = injector.instance[FinatraObjectMapper]

}
