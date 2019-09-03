package io.github.cactacea.backend.core.helpers.specs

import com.twitter.inject.app.TestInjector
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.util.modules.DefaultCoreModule

class RepositorySpec extends SpecHelper with UtilHelper {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultCoreModule,
        DefaultChatModule,
        DefaultMessageModule,
        DefaultQueueModule,
        DefaultMobilePushModule,
        DefaultStorageModule,
        DefaultDeepLinkModule,
        DefaultJacksonModule
      )
    ).create

  val db = injector.instance[DatabaseService]

  val usersRepository = injector.instance[UsersRepository]
  val userChannelsRepository = injector.instance[UserChannelRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val commentsRepository = injector.instance[CommentsRepository]
  val commentLikesRepository = injector.instance[CommentLikesRepository]
  val devicesRepository = injector.instance[DevicesRepository]
  val feedsRepository = injector.instance[FeedsRepository]
  val feedLikesRepository = injector.instance[FeedLikesRepository]
  val followsRepository = injector.instance[FollowsRepository]
  val followersRepository = injector.instance[FollowersRepository]
  val friendsRepository = injector.instance[FriendsRepository]
  val friendRequestsRepository = injector.instance[FriendRequestsRepository]
  var channelsRepository = injector.instance[ChannelsRepository]
  val channelUsersRepository = injector.instance[ChannelUsersRepository]
  val invitationsRepository = injector.instance[InvitationsRepository]
  val mediumsRepository = injector.instance[MediumsRepository]
  val mutesRepository = injector.instance[MutesRepository]
  val messagesRepository = injector.instance[MessagesRepository]
  val notificationsRepository = injector.instance[NotificationsRepository]
  val pushNotificationSettingsRepository = injector.instance[PushNotificationSettingsRepository]

  val usersDAO = injector.instance[UsersDAO]
  val userFeedsDAO = injector.instance[UserFeedsDAO]
  val userChannelsDAO = injector.instance[UserChannelsDAO]
  val userMessagesDAO = injector.instance[UserMessagesDAO]
  val userReportsDAO = injector.instance[UserReportsDAO]
  val userAuthenticationsDAO = injector.instance[UserAuthenticationsDAO]
  val blocksDAO = injector.instance[BlocksDAO]
  val commentsDAO = injector.instance[CommentsDAO]
  val commentLikesDAO = injector.instance[CommentLikesDAO]
  val commentReportsDAO = injector.instance[CommentReportsDAO]
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
  val channelReportsDAO = injector.instance[ChannelReportsDAO]
  val invitationsDAO = injector.instance[InvitationsDAO]
  val mediumsDAO = injector.instance[MediumsDAO]
  val messagesDAO = injector.instance[MessagesDAO]
  val mutesDAO = injector.instance[MutesDAO]
  val pushNotificationSettingDAO = injector.instance[PushNotificationSettingsDAO]
  val pushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val pushNotificationFeedsDAO = injector.instance[PushNotificationFeedsDAO]
  val pushNotificationCommentsDAO = injector.instance[PushNotificationCommentsDAO]
  val pushNotificationMessagesDAO = injector.instance[PushNotificationMessagesDAO]
  val pushNotificationInvitationsDAO = injector.instance[PushNotificationInvitationsDAO]
  val pushNotificationRequestsDAO = injector.instance[PushNotificationRequestsDAO]

  def createUser(userName: String): Future[User] = {
    usersRepository.create("credentails", userName, userName, None)
  }

}
