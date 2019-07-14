package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.app.TestInjector
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.utils.auth.CactaceaAuthModule

@Singleton
class CactaceaServerSpec extends FeatureTest
  with AccountsControllerSpec
  with BlocksControllerSpec
  with CommentLikesControllerSpec
  with CommentsControllerSpec
  with FeedLikesControllerSpec
  with FeedsControllerSpec
  with FollowsControllerSpec
  with FriendsControllerSpec
  with GroupsControllerSpec
  with InvitationsControllerSpec
  with MessagesControllerSpec
  with MutesControllerSpec
  with NotificationsControllerSpec
  with FriendRequestsControllerSpec
  with SessionControllerSpec
  with SessionsControllerSpec
  with SettingsControllerSpec {

  override val server = new EmbeddedHttpServer(
    twitterServer = new CactaceaServer {
      addFrameworkModule(CactaceaAuthModule)

    }
  )

  override val injector =
    TestInjector(
      modules = Seq(
          DatabaseModule,
        CactaceaAuthModule,
          DefaultChatModule,
          DefaultDeepLinkModule,
          DefaultJacksonModule,
          DefaultListenerModule,
          DefaultMessageModule,
          DefaultMobilePushModule,
          DefaultQueueModule,
          DefaultStorageModule
      )
    ).create

  val mapper = injector.instance[FinatraObjectMapper]

}
