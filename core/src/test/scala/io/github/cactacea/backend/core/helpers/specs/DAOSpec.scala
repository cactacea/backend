package io.github.cactacea.backend.core.helpers.specs

import com.twitter.inject.app.TestInjector
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.modules.{DatabaseModule, DefaultDeepLinkModule, DefaultMessageModule}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.helpers.generators.{ModelsGenerator, StatusGenerator}
import io.github.cactacea.backend.core.helpers.utils.DAOHelper
import io.github.cactacea.backend.core.infrastructure.dao._

class DAOSpec extends Spec
  with StatusGenerator
  with ModelsGenerator
  with DAOHelper {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultMessageModule,
        DefaultDeepLinkModule
      )
    ).create

  val db = injector.instance[DatabaseService]

  val usersDAO = injector.instance[UsersDAO]
  val userTweetsDAO = injector.instance[UserTweetsDAO]
  val userChannelsDAO = injector.instance[UserChannelsDAO]
  val userMessagesDAO = injector.instance[UserMessagesDAO]
  val userReportsDAO = injector.instance[UserReportsDAO]
  val blocksDAO = injector.instance[BlocksDAO]
  val commentsDAO = injector.instance[CommentsDAO]
  val commentLikesDAO = injector.instance[CommentLikesDAO]
  val commentReportsDAO = injector.instance[CommentReportsDAO]
  val clientsDAO = injector.instance[ClientsDAO]
  val devicesDAO = injector.instance[DevicesDAO]
  val tweetsDAO = injector.instance[TweetsDAO]
  val tweetTagsDAO = injector.instance[TweetTagsDAO]
  val tweetMediumsDAO = injector.instance[TweetMediumsDAO]
  val tweetLikesDAO = injector.instance[TweetLikesDAO]
  val tweetReportsDAO = injector.instance[TweetReportsDAO]
  val followsDAO = injector.instance[FollowsDAO]
  val followersDAO = injector.instance[FollowersDAO]
  val friendsDAO = injector.instance[FriendsDAO]
  val friendRequestsDAO = injector.instance[FriendRequestsDAO]
  val channelsDAO = injector.instance[ChannelsDAO]
  val channelUsersDAO = injector.instance[ChannelUsersDAO]
  val invitationsDAO = injector.instance[InvitationsDAO]
  val channelReportsDAO = injector.instance[ChannelReportsDAO]
  val mediumsDAO = injector.instance[MediumsDAO]
  val messagesDAO = injector.instance[MessagesDAO]
  val mutesDAO = injector.instance[MutesDAO]
  val notificationsDAO = injector.instance[FeedsDAO]
  val notificationSettingsDAO = injector.instance[NotificationSettingsDAO]

  val deepLinkService = injector.instance[DeepLinkService]

}
