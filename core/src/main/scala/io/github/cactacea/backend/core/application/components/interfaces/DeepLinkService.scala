package io.github.cactacea.backend.core.application.components.interfaces

import io.github.cactacea.backend.core.infrastructure.identifiers._

trait DeepLinkService {

  def getAccounts(): String
  def getAccount(id: AccountId): String
  def getFeeds(): String
  def getFeed(id: FeedId): String
  def getComments(id: FeedId): String
  def getComment(feedId: FeedId, commentId: CommentId): String
  def getRequests(): String
  def getRequest(id: FriendRequestId): String
  def getInvitations(): String
  def getInvitation(id: InvitationId): String
  def getGroups(): String
  def getGroup(id: GroupId): String
  def getMessages(groupId: GroupId, id: MessageId): String
  def getNotification(id: NotificationId): String

}
