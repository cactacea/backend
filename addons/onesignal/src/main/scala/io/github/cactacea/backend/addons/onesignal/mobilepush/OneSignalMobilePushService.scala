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
                                            notificationsRepository: NotificationsRepository

                                ) extends MobilePushService {

  val numberOfChannels = 100

  def sendTweet(id: TweetId): Future[Unit] = {
    notificationsRepository.findTweet(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentSeq(notifications)
        sendContentSeq(list).map({ result =>
          Future.traverseSequentially(result) { ids =>
            notificationsRepository.updateUserTweetsNotified(id, ids)
          }.map(_ =>
            if (result.size == list.size) {
              notificationsRepository.updateTweetNotified(id)
            } else {
              Future.Done
            }
          )
        })
      case None =>
        db.transaction {
          notificationsRepository.updateTweetNotified(id)
        }
    })
  }

  def sendMessage(id: MessageId): Future[Unit] = {
    notificationsRepository.findMessages(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentSeq(notifications)
        sendContentSeq(list).map({ result =>
          Future.traverseSequentially(result) { ids =>
            notificationsRepository.updateUserMessageNotified(id, ids)
          }.map(_ =>
            if (result.size == list.size) {
              notificationsRepository.updateMessageNotified(id)
            } else {
              Future.Done
            }
          )
        })
      case None =>
        db.transaction {
          notificationsRepository.updateMessageNotified(id)
        }
    })
  }

  def sendComment(id: CommentId): Future[Unit] = {
    notificationsRepository.findComment(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(notificationsRepository.updateCommentNotified(id))
        } yield (r)
      case None =>
        db.transaction {
          notificationsRepository.updateCommentNotified(id)
        }
    })
  }

  def sendFriendRequest(id: FriendRequestId): Future[Unit] = {
    notificationsRepository.findRequests(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(notificationsRepository.updateRequestNotified(id))
        } yield (r)
      case None =>
        db.transaction {
          notificationsRepository.updateRequestNotified(id)
        }
    })
  }

  def sendInvitation(id: InvitationId): Future[Unit] = {
    notificationsRepository.findInvitations(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(notificationsRepository.updateInvitationNotified(id))
        } yield (r)
      case None =>
        db.transaction {
          notificationsRepository.updateInvitationNotified(id)
        }
    })
  }



  private def createContentSeq(notifications: Seq[Notification]): Seq[(OneSignalNotification, Seq[UserId])] = {
    notifications.flatMap({ notification =>
      val displayName = notification.displayName
      val message = notification.message
      val en = messageService.getNotificationMessage(notification.notificationType, Seq(Locale.US), displayName, message)
      val jp = messageService.getNotificationMessage(notification.notificationType, Seq(Locale.JAPAN), displayName, message)
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

