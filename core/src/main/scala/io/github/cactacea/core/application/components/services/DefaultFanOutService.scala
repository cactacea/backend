package io.github.cactacea.core.application.components.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{FanOutService, PushNotificationService}
import io.github.cactacea.core.domain.repositories.PushNotificationsRepository
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.util.exceptions.FanOutException

@Singleton
class DefaultFanOutService extends FanOutService {

  @Inject private var pushNotificationService: PushNotificationService = _
  @Inject private var pushNotificationsRepository: PushNotificationsRepository = _

  def fanOutFeed(feedId: FeedId): Future[Unit] = {
    (for {
      p <- pushNotificationsRepository.findFeeds(feedId)
      a <- pushNotificationService.send(p)
      u <- pushNotificationsRepository.updateFeeds(feedId, a)
    } yield (u)).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(new FanOutException())
    })
  }

  def fanOutComment(commentId: CommentId): Future[Unit] = {
    (for {
      p <- pushNotificationsRepository.findComments(commentId)
      _ <- pushNotificationService.send(p)
      u <- pushNotificationsRepository.updateComments(commentId)
    } yield (u)).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(new FanOutException())
    })
  }

  def fanOutMessage(messageId: MessageId): Future[Unit] = {
    (for {
      p <- pushNotificationsRepository.findMessages(messageId)
      a <- pushNotificationService.send(p)
      u <- pushNotificationsRepository.updateMessages(messageId, a)
    } yield (u)).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(new FanOutException())
    })
  }

  def fanOutGroupInvite(groupInvitationId: GroupInvitationId): Future[Unit] = {
    (for {
      p <- pushNotificationsRepository.findGroupInvites(groupInvitationId)
      _ <- pushNotificationService.send(p)
      u <- pushNotificationsRepository.updateGroupInvites(groupInvitationId)
    } yield (u)).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(new FanOutException())
    })
  }

  def fanOutFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    Future.Unit
  }

  def fanOutGroup(groupId: GroupId): Future[Unit] = {
    Future.Unit
  }
  def fanOutFavoriteFeed(feedId: FeedId): Future[Unit] = {
    Future.Unit
  }

}
