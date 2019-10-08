package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.MobilePushService
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultMobilePushService @Inject()(notificationsRepository: NotificationsRepository) extends MobilePushService {

  def sendTweet(id: TweetId): Future[Unit] = {
    notificationsRepository.findTweet(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationsRepository.updateTweetNotified(id)
          _ <- notificationsRepository.updateUserTweetsNotified(id, l.map(_.destinations.map(_.userId)).flatten)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendMessage(id: MessageId): Future[Unit] = {
    notificationsRepository.findMessages(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationsRepository.updateMessageNotified(id)
          _ <- notificationsRepository.updateUserMessageNotified(id, l.map(_.destinations.map(_.userId)).flatten)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendComment(id: CommentId): Future[Unit] = {
    notificationsRepository.findComment(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationsRepository.updateCommentNotified(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendFriendRequest(id: FriendRequestId): Future[Unit] = {
    notificationsRepository.findRequests(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationsRepository.updateRequestNotified(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendInvitation(id: InvitationId): Future[Unit] = {
    notificationsRepository.findInvitations(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationsRepository.updateInvitationNotified(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

}
