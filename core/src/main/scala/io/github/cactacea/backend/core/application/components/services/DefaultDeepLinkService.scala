package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultDeepLinkService @Inject()(@Flag("scheme") scheme: String) extends DeepLinkService {

  def getAccounts(): String = {
    s"${scheme}://accounts"
  }

  def getAccount(id: AccountId): String = {
    s"${scheme}://accounts/:id"
  }

  def getFeeds(): String = {
    s"${scheme}://feeds"
  }

  def getFeed(id: FeedId): String = {
    s"${scheme}://feeds/${id}"
  }

  def getComments(id: FeedId): String = {
    s"${scheme}://feeds/${id}/comments"
  }

  def getComment(feedId: FeedId, commentId: CommentId): String = {
    s"${scheme}://feeds/${feedId}/comments/${commentId}"
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

  def getGroups(): String = {
    s"${scheme}://groups"
  }

  def getGroup(id: GroupId): String = {
    s"${scheme}://groups/${id}"
  }

  def getMessages(groupId: GroupId, messageId: MessageId): String = {
    s"${scheme}://groups/${groupId}/messages/${messageId}"
  }

  def getNotification(id: NotificationId): String = {
    s"${scheme}://notifications/${id}"
  }

}
