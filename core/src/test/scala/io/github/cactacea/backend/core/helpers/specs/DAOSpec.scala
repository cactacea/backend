package io.github.cactacea.backend.core.helpers.specs

import com.twitter.inject.app.TestInjector
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.modules.{DatabaseModule, DefaultDeepLinkModule, DefaultMessageModule}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.dao.{AuthenticationsDAO, _}

class DAOSpec extends SpecHelper with UtilHelper {

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
  val userFeedsDAO = injector.instance[UserFeedsDAO]
  val userChannelsDAO = injector.instance[UserChannelsDAO]
  val userMessagesDAO = injector.instance[UserMessagesDAO]
  val userReportsDAO = injector.instance[UserReportsDAO]
  val authenticationsDAO = injector.instance[AuthenticationsDAO]
  val blocksDAO = injector.instance[BlocksDAO]
  val commentsDAO = injector.instance[CommentsDAO]
  val commentLikesDAO = injector.instance[CommentLikesDAO]
  val commentReportsDAO = injector.instance[CommentReportsDAO]
  val clientsDAO = injector.instance[ClientsDAO]
  val devicesDAO = injector.instance[DevicesDAO]
  val feedsDAO = injector.instance[FeedsDAO]
  val feedTagsDAO = injector.instance[FeedTagsDAO]
  val feedMediumsDAO = injector.instance[FeedMediumsDAO]
  val feedLikesDAO = injector.instance[FeedLikesDAO]
  val feedReportsDAO = injector.instance[FeedReportsDAO]
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
  val notificationsDAO = injector.instance[NotificationsDAO]
  val pushNotificationSettingDAO = injector.instance[PushNotificationSettingsDAO]
  val pushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val pushNotificationFeedsDAO = injector.instance[PushNotificationFeedsDAO]
  val pushNotificationCommentsDAO = injector.instance[PushNotificationCommentsDAO]
  val pushNotificationMessagesDAO = injector.instance[PushNotificationMessagesDAO]
  val pushNotificationInvitationsDAO = injector.instance[PushNotificationInvitationsDAO]
  val pushNotificationFriendRequestsDAO = injector.instance[PushNotificationRequestsDAO]

  val deepLinkService = injector.instance[DeepLinkService]

}
