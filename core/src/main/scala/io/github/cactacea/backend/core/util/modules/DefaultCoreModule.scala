package io.github.cactacea.backend.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.repositories._

object DefaultCoreModule extends TwitterModule {

  def configureRepository(): Unit = {
    bindSingleton[UserChannelRepository]
    bindSingleton[UsersRepository]
    bindSingleton[BlocksRepository]
    bindSingleton[CommentLikesRepository]
    bindSingleton[CommentsRepository]
    bindSingleton[DevicesRepository]
    bindSingleton[TweetLikesRepository]
    bindSingleton[TweetsRepository]
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
    bindSingleton[FeedsRepository]
    bindSingleton[NotificationCommentsRepository]
    bindSingleton[NotificationTweetsRepository]
    bindSingleton[NotificationFriendRequestsRepository]
    bindSingleton[NotificationInvitationsRepository]
    bindSingleton[NotificationMessagesRepository]
    bindSingleton[NotificationSettingsRepository]
  }

  def configureService(): Unit = {
    bindSingleton[UserChannelsService]
    bindSingleton[UsersService]
    bindSingleton[BlocksService]
    bindSingleton[CommentLikesService]
    bindSingleton[CommentsService]
    bindSingleton[DevicesService]
    bindSingleton[TweetLikesService]
    bindSingleton[TweetsService]
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
    bindSingleton[FeedsService]
    bindSingleton[SettingsService]
  }

  override def configure(): Unit = {
//    configureRepository()
//    configureService()
  }
}
