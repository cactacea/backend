package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.MobilePushService
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultMobilePushService @Inject()(
                                          notificationTweetsRepository: NotificationTweetsRepository,
                                          notificationCommentsRepository: NotificationCommentsRepository,
                                          notificationMessagesRepository: NotificationMessagesRepository,
                                          notificationFriendRequestsRepository: NotificationFriendRequestsRepository,
                                          notificationInvitationsRepository: NotificationInvitationsRepository

                                        ) extends MobilePushService {

  def sendTweet(id: TweetId): Future[Unit] = {
    notificationTweetsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationTweetsRepository.update(id)
          _ <- notificationTweetsRepository.update(id, l.map(_.destinations.map(_.userId)).flatten)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendMessage(id: MessageId): Future[Unit] = {
    notificationMessagesRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationMessagesRepository.update(id)
          _ <- notificationMessagesRepository.update(id, l.map(_.destinations.map(_.userId)).flatten)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendComment(id: CommentId): Future[Unit] = {
    notificationCommentsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationCommentsRepository.update(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendFriendRequest(id: FriendRequestId): Future[Unit] = {
    notificationFriendRequestsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationFriendRequestsRepository.update(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendInvitation(id: InvitationId): Future[Unit] = {
    notificationInvitationsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- notificationInvitationsRepository.update(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

}
