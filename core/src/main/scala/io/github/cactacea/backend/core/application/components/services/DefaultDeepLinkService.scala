package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultDeepLinkService @Inject()(@Flag("scheme") scheme: String) extends DeepLinkService {

  def getUsers(): String = {
    s"${scheme}://users"
  }

  def getUser(id: UserId): String = {
    s"${scheme}://users/:id"
  }

  def getTweets(): String = {
    s"${scheme}://tweets"
  }

  def getTweet(id: TweetId): String = {
    s"${scheme}://tweets/${id}"
  }

  def getComments(id: TweetId): String = {
    s"${scheme}://tweets/${id}/comments"
  }

  def getComment(tweetId: TweetId, commentId: CommentId): String = {
    s"${scheme}://tweets/${tweetId}/comments/${commentId}"
  }

  def getRequests(): String = {
    s"${scheme}://requests"
  }

  def getRequest(id: FriendRequestId): String = {
    s"${scheme}://requests/${id}"
  }

  def getInvitations(): String = {
    s"${scheme}://invitations"
  }

  def getInvitation(id: InvitationId): String = {
    s"${scheme}://invitations/${id}"
  }

  def getChannels(): String = {
    s"${scheme}://channels"
  }

  def getChannel(id: ChannelId): String = {
    s"${scheme}://channels/${id}"
  }

  def getMessages(channelId: ChannelId, messageId: MessageId): String = {
    s"${scheme}://channels/${channelId}/messages/${messageId}"
  }

  def getNotification(id: NotificationId): String = {
    s"${scheme}://notifications/${id}"
  }

}
