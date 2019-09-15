package io.github.cactacea.backend.core.application.components.interfaces

import io.github.cactacea.backend.core.infrastructure.identifiers._

trait DeepLinkService {

  def getUsers(): String
  def getUser(id: UserId): String
  def getTweets(): String
  def getTweet(id: TweetId): String
  def getComments(id: TweetId): String
  def getComment(tweetId: TweetId, commentId: CommentId): String
  def getRequests(): String
  def getRequest(id: FriendRequestId): String
  def getInvitations(): String
  def getInvitation(id: InvitationId): String
  def getChannels(): String
  def getChannel(id: ChannelId): String
  def getMessages(channelId: ChannelId, id: MessageId): String
  def getNotification(id: NotificationId): String

}
