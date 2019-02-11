package io.github.cactacea.addons.onesignal.mobilepush

import java.util.Locale

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.addons.onesignal.utils.{OneSignalClient, OneSignalConfig, OneSignalNotification}
import io.github.cactacea.backend.core.application.components.interfaces.{MessageService, MobilePushService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers._

class OneSignalMobilePushService @Inject()(
                                  db: DatabaseService,
                                  client: OneSignalClient,
                                  messageService: MessageService,
                                  pushNotificationFeedsRepository: PushNotificationFeedsRepository,
                                  pushNotificationCommentsRepository: PushNotificationCommentsRepository,
                                  pushNotificationMessagesRepository: PushNotificationMessagesRepository,
                                  pushNotificationFriendRequestsRepository: PushNotificationFriendRequestsRepository,
                                  pushNotificationGroupInvitationsRepository: PushNotificationGroupInvitationsRepository

                                ) extends MobilePushService {

  val numberOfGrouped = 100

  def sendFeed(id: FeedId): Future[Unit] = {
    pushNotificationFeedsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentList(notifications)
        sendContentList(list).map({ result =>
          Future.traverseSequentially(result) { ids =>
            pushNotificationFeedsRepository.update(id, ids)
          }.map(_ =>
            if (result.size == list.size) {
              pushNotificationFeedsRepository.update(id)
            } else {
              Future.Done
            }
          )
        })
      case None =>
        db.transaction {
          pushNotificationFeedsRepository.update(id)
        }
    })
  }

  def sendMessage(id: MessageId): Future[Unit] = {
    pushNotificationMessagesRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentList(notifications)
        sendContentList(list).map({ result =>
          Future.traverseSequentially(result) { ids =>
            pushNotificationMessagesRepository.update(id, ids)
          }.map(_ =>
            if (result.size == list.size) {
              pushNotificationMessagesRepository.update(id)
            } else {
              Future.Done
            }
          )
        })
      case None =>
        db.transaction {
          pushNotificationMessagesRepository.update(id)
        }
    })
  }

  def sendComment(id: CommentId): Future[Unit] = {
    pushNotificationCommentsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentList(createContentList(notifications))
          r <- db.transaction(pushNotificationCommentsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          pushNotificationCommentsRepository.update(id)
        }
    })
  }

  def sendFriendRequest(id: FriendRequestId): Future[Unit] = {
    pushNotificationFriendRequestsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentList(createContentList(notifications))
          r <- db.transaction(pushNotificationFriendRequestsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          pushNotificationFriendRequestsRepository.update(id)
        }
    })
  }

  def sendGroupInvitation(id: GroupInvitationId): Future[Unit] = {
    pushNotificationGroupInvitationsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentList(createContentList(notifications))
          r <- db.transaction(pushNotificationGroupInvitationsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          pushNotificationGroupInvitationsRepository.update(id)
        }
    })
  }



  private def createContentList(notifications: List[PushNotification]): List[(OneSignalNotification, List[AccountId])] = {
    notifications.flatMap({ notification =>
      val displayName = notification.displayName
      val message = notification.message
      val en = messageService.getPushNotificationMessage(notification.notificationType, Seq(Locale.US), displayName, message)
      val jp = messageService.getPushNotificationMessage(notification.notificationType, Seq(Locale.JAPAN), displayName, message)
      val url = notification.url
      notification.destinations.grouped(numberOfGrouped).map({ groupedDestinations =>
        val accessTokens = groupedDestinations.map(_.accountToken)
        val accountIds =  groupedDestinations.map(_.accountId)
        val content = OneSignalNotification(OneSignalConfig.onesignal.appId, accessTokens, en, jp, url)
        (content, accountIds)
      })
    })
  }

  private def sendContentList(l: List[(OneSignalNotification, List[AccountId])]): Future[List[List[AccountId]]] = {
    val result = Future.traverseSequentially(l) { case (content, accountIds) =>
      client.createNotification(content).flatMap(response =>
        if (response.statusCode >= 200 && response.statusCode <= 299) {
          Future.value(Some(accountIds))
        } else {
          Future.None
        }
      )
    }
    result.map(_.flatten.toList)
  }


}

