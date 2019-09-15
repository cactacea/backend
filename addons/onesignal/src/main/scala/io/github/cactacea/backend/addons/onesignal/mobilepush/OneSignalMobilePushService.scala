package io.github.cactacea.backend.addons.onesignal.mobilepush

import java.util.Locale

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.addons.onesignal.utils.{OneSignalClient, OneSignalConfig, OneSignalNotification}
import io.github.cactacea.backend.core.application.components.interfaces.{MessageService, MobilePushService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers._

class OneSignalMobilePushService @Inject()(
                                            db: DatabaseService,
                                            client: OneSignalClient,
                                            messageService: MessageService,
                                            notificationTweetsRepository: NotificationTweetsRepository,
                                            notificationCommentsRepository: NotificationCommentsRepository,
                                            notificationMessagesRepository: NotificationMessagesRepository,
                                            notificationFriendRequestsRepository: NotificationFriendRequestsRepository,
                                            notificationInvitationsRepository: NotificationInvitationsRepository

                                ) extends MobilePushService {

  val numberOfChannels = 100

  def sendTweet(id: TweetId): Future[Unit] = {
    notificationTweetsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentSeq(notifications)
        sendContentSeq(list).map({ result =>
          Future.traverseSequentially(result) { ids =>
            notificationTweetsRepository.update(id, ids)
          }.map(_ =>
            if (result.size == list.size) {
              notificationTweetsRepository.update(id)
            } else {
              Future.Done
            }
          )
        })
      case None =>
        db.transaction {
          notificationTweetsRepository.update(id)
        }
    })
  }

  def sendMessage(id: MessageId): Future[Unit] = {
    notificationMessagesRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentSeq(notifications)
        sendContentSeq(list).map({ result =>
          Future.traverseSequentially(result) { ids =>
            notificationMessagesRepository.update(id, ids)
          }.map(_ =>
            if (result.size == list.size) {
              notificationMessagesRepository.update(id)
            } else {
              Future.Done
            }
          )
        })
      case None =>
        db.transaction {
          notificationMessagesRepository.update(id)
        }
    })
  }

  def sendComment(id: CommentId): Future[Unit] = {
    notificationCommentsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(notificationCommentsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          notificationCommentsRepository.update(id)
        }
    })
  }

  def sendFriendRequest(id: FriendRequestId): Future[Unit] = {
    notificationFriendRequestsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(notificationFriendRequestsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          notificationFriendRequestsRepository.update(id)
        }
    })
  }

  def sendInvitation(id: InvitationId): Future[Unit] = {
    notificationInvitationsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(notificationInvitationsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          notificationInvitationsRepository.update(id)
        }
    })
  }



  private def createContentSeq(notifications: Seq[Notification]): Seq[(OneSignalNotification, Seq[UserId])] = {
    notifications.flatMap({ notification =>
      val displayName = notification.displayName
      val message = notification.message
      val en = messageService.getNotificationMessage(notification.feedType, Seq(Locale.US), displayName, message)
      val jp = messageService.getNotificationMessage(notification.feedType, Seq(Locale.JAPAN), displayName, message)
      val url = notification.url
      notification.destinations.grouped(numberOfChannels).map({ channelsDestinations =>
        val accessTokens = channelsDestinations.map(_.userToken)
        val accountIds =  channelsDestinations.map(_.userId)
        val content = OneSignalNotification(OneSignalConfig.onesignal.appId, accessTokens, en, jp, url)
        (content, accountIds)
      })
    })
  }

  private def sendContentSeq(l: Seq[(OneSignalNotification, Seq[UserId])]): Future[Seq[Seq[UserId]]] = {
    val result = Future.traverseSequentially(l) { case (content, accountIds) =>
      client.createNotification(content).flatMap(response =>
        if (response.statusCode >= 200 && response.statusCode <= 299) {
          Future.value(Some(accountIds))
        } else {
          Future.None
        }
      )
    }
    result.map(_.flatten.toSeq)
  }


}

