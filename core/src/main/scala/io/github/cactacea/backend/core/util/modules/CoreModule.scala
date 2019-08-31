package io.github.cactacea.backend.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.repositories.{AccountChannelRepository, AccountsRepository, AuthenticationsRepository, BlocksRepository, CommentLikesRepository, CommentsRepository, DevicesRepository, FeedLikesRepository, FeedsRepository, FollowersRepository, FollowsRepository, FriendRequestsRepository, FriendsRepository, ChannelAccountsRepository, InvitationsRepository, ChannelsRepository, MediumsRepository, MessagesRepository, MutesRepository, NotificationsRepository, PushNotificationCommentsRepository, PushNotificationFeedsRepository, PushNotificationFriendRequestsRepository, PushNotificationInvitationsRepository, PushNotificationMessagesRepository, PushNotificationSettingsRepository}

object CoreModule extends TwitterModule {

  def configureRepository(): Unit = {
    bindSingleton[AccountChannelRepository]
    bindSingleton[AccountsRepository]
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
    bindSingleton[ChannelAccountsRepository]
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
    bindSingleton[AccountChannelsService]
    bindSingleton[AccountsService]
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
    bindSingleton[ChannelAccountsService]
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
