package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class NotificationsRepository @Inject()(
                                         commentsDAO: CommentsDAO,
                                         friendRequestsDAO: FriendRequestsDAO,
                                         invitationsDAO: InvitationsDAO,
                                         messagesDAO: MessagesDAO,
                                         tweetsDAO: TweetsDAO,
                                         userTweetsDAO: UserTweetsDAO,
                                         userMessagesDAO: UserMessagesDAO
                                       ) {

  def findComment(id: CommentId): Future[Option[Seq[Notification]]] = {
    commentsDAO.findNotifications(id)
  }

  def updateCommentNotified(id: CommentId): Future[Unit] = {
    commentsDAO.updateNotified(id)
  }

  def findTweet(tweetId: TweetId): Future[Option[Seq[Notification]]] = {
    tweetsDAO.findNotifications(tweetId)
  }

  def updateTweetNotified(tweetId: TweetId): Future[Unit] = {
    tweetsDAO.updateNotified(tweetId)
  }

  def updateUserTweetsNotified(tweetId: TweetId, userIds: Seq[UserId]): Future[Unit] = {
    userTweetsDAO.updateNotified(tweetId, userIds)
  }

  def findRequests(id: FriendRequestId): Future[Option[Seq[Notification]]] = {
    friendRequestsDAO.findNotifications(id)
  }

  def updateRequestNotified(id: FriendRequestId): Future[Unit] = {
    friendRequestsDAO.updateNotified(id)
  }

  def findInvitations(id: InvitationId): Future[Option[Seq[Notification]]] = {
    invitationsDAO.findInvitations(id)
  }

  def updateInvitationNotified(id: InvitationId): Future[Unit] = {
    invitationsDAO.updateNotified(id)
  }

  def findMessages(id: MessageId): Future[Option[Seq[Notification]]] = {
    messagesDAO.findNotifications(id)
  }

  def updateMessageNotified(id: MessageId): Future[Unit] = {
    messagesDAO.updateNotified(id)
  }

  def updateUserMessageNotified(id: MessageId, userIds: Seq[UserId]): Future[Unit] = {
    userMessagesDAO.updateNotified(id, userIds)
  }

}
