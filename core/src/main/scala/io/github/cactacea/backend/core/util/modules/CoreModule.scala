package io.github.cactacea.backend.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.repositories.{AccountGroupsRepository, AccountsRepository, AuthenticationsRepository, BlocksRepository, CommentLikesRepository, CommentsRepository, DevicesRepository, FeedLikesRepository, FeedsRepository, FollowersRepository, FollowsRepository, FriendRequestsRepository, FriendsRepository, GroupAccountsRepository, InvitationsRepository, GroupsRepository, MediumsRepository, MessagesRepository, MutesRepository, NotificationsRepository, PushNotificationCommentsRepository, PushNotificationFeedsRepository, PushNotificationFriendRequestsRepository, PushNotificationInvitationsRepository, PushNotificationMessagesRepository, PushNotificationSettingsRepository}

object CoreModule extends TwitterModule {

  def configureRepository(): Unit = {
    bindSingleton[AccountGroupsRepository]
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
    bindSingleton[GroupAccountsRepository]
    bindSingleton[InvitationsRepository]
    bindSingleton[GroupsRepository]
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
    bindSingleton[AccountGroupsService]
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
    bindSingleton[GroupAccountsService]
    bindSingleton[InvitationsService]
    bindSingleton[GroupsService]
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
