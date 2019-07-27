package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.MobilePushService
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultMobilePushService @Inject()(
                                          pushNotificationFeedsRepository: PushNotificationFeedsRepository,
                                          pushNotificationCommentsRepository: PushNotificationCommentsRepository,
                                          pushNotificationMessagesRepository: PushNotificationMessagesRepository,
                                          pushNotificationFriendRequestsRepository: PushNotificationFriendRequestsRepository,
                                          pushNotificationInvitationsRepository: PushNotificationInvitationsRepository

                                        ) extends MobilePushService {

  def sendFeed(id: FeedId): Future[Unit] = {
    pushNotificationFeedsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- pushNotificationFeedsRepository.update(id)
          _ <- pushNotificationFeedsRepository.update(id, l.map(_.destinations.map(_.accountId)).flatten)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendMessage(id: MessageId): Future[Unit] = {
    pushNotificationMessagesRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- pushNotificationMessagesRepository.update(id)
          _ <- pushNotificationMessagesRepository.update(id, l.map(_.destinations.map(_.accountId)).flatten)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendComment(id: CommentId): Future[Unit] = {
    pushNotificationCommentsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- pushNotificationCommentsRepository.update(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendFriendRequest(id: FriendRequestId): Future[Unit] = {
    pushNotificationFriendRequestsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- pushNotificationFriendRequestsRepository.update(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

  def sendInvitation(id: InvitationId): Future[Unit] = {
    pushNotificationInvitationsRepository.find(id).flatMap(_ match {
      case Some(l) =>
        println("----- Push Notification ----") // scalastyle:ignore
        println(l) // scalastyle:ignore
        for {
          _ <- pushNotificationInvitationsRepository.update(id)
        } yield (())

      case None =>
        println("----- Push Notification ----") // scalastyle:ignore
        println("No destinations") // scalastyle:ignore
        Future.Done
    })
  }

}
