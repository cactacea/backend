package io.github.cactacea.backend.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.repositories.{UserChannelRepository, UsersRepository, AuthenticationsRepository, BlocksRepository, CommentLikesRepository, CommentsRepository, DevicesRepository, FeedLikesRepository, FeedsRepository, FollowersRepository, FollowsRepository, FriendRequestsRepository, FriendsRepository, ChannelUsersRepository, InvitationsRepository, ChannelsRepository, MediumsRepository, MessagesRepository, MutesRepository, NotificationsRepository, PushNotificationCommentsRepository, PushNotificationFeedsRepository, PushNotificationFriendRequestsRepository, PushNotificationInvitationsRepository, PushNotificationMessagesRepository, PushNotificationSettingsRepository}

object CactaceaCoreModule extends TwitterModule {

  def configureRepository(): Unit = {
    bindSingleton[UserChannelRepository]
    bindSingleton[UsersRepository]
    bindSingleton[AuthenticationsRepository]
    bindSingleton[BlocksRepository]
    bindSingleton[CommentLikesRepository]
    bindSingleton[CommentsRepository]
    bindSingleton[DevicesRepository]
    bindSingleton[FeedLikesRepository]
    bindSingleton[FeedsRepository]
    bindSingleton[FollowersRepository]
    bindSingleton[FollowsRepository]
    bindSingleton[FriendRequestsRepository]
    bindSingleton[FriendsRepository]
    bindSingleton[ChannelUsersRepository]
    bindSingleton[InvitationsRepository]
    bindSingleton[ChannelsRepository]
    bindSingleton[MediumsRepository]
    bindSingleton[MessagesRepository]
    bindSingleton[MutesRepository]
    bindSingleton[NotificationsRepository]
    bindSingleton[PushNotificationCommentsRepository]
    bindSingleton[PushNotificationFeedsRepository]
    bindSingleton[PushNotificationFriendRequestsRepository]
    bindSingleton[PushNotificationInvitationsRepository]
    bindSingleton[PushNotificationMessagesRepository]
    bindSingleton[PushNotificationSettingsRepository]
  }

  def configureService(): Unit = {
    bindSingleton[UserChannelsService]
    bindSingleton[UsersService]
    bindSingleton[BlocksService]
    bindSingleton[CommentLikesService]
    bindSingleton[CommentsService]
    bindSingleton[DevicesService]
    bindSingleton[FeedLikesService]
    bindSingleton[FeedsService]
    bindSingleton[FollowersService]
    bindSingleton[FollowsService]
    bindSingleton[FriendRequestsService]
    bindSingleton[FriendsService]
    bindSingleton[ChannelUsersService]
    bindSingleton[InvitationsService]
    bindSingleton[ChannelsService]
    bindSingleton[MediumsService]
    bindSingleton[MessagesService]
    bindSingleton[MutesService]
    bindSingleton[NotificationsService]
    bindSingleton[SettingsService]
  }

  override def configure(): Unit = {
    configureRepository()
    configureService()
  }
}
