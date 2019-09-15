package io.github.cactacea.backend.addons.onesignal.mobilepush

import java.util.Locale

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.addons.onesignal.utils.{OneSignalClient, OneSignalConfig, OneSignalNotification}
import io.github.cactacea.backend.core.application.components.interfaces.{MessageService, MobilePushService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers._

class OneSignalMobilePushService @Inject()(
                                            db: DatabaseService,
                                            client: OneSignalClient,
                                            messageService: MessageService,
                                            pushNotificationTweetsRepository: PushNotificationTweetsRepository,
                                            pushNotificationCommentsRepository: PushNotificationCommentsRepository,
                                            pushNotificationMessagesRepository: PushNotificationMessagesRepository,
                                            pushNotificationFriendRequestsRepository: PushNotificationFriendRequestsRepository,
                                            pushNotificationInvitationsRepository: PushNotificationInvitationsRepository

                                ) extends MobilePushService {

  val numberOfChannels = 100

  def sendTweet(id: TweetId): Future[Unit] = {
    pushNotificationTweetsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentSeq(notifications)
        sendContentSeq(list).map({ result =>
          Future.traverseSequentially(result) { ids =>
            pushNotificationTweetsRepository.update(id, ids)
          }.map(_ =>
            if (result.size == list.size) {
              pushNotificationTweetsRepository.update(id)
            } else {
              Future.Done
            }
          )
        })
      case None =>
        db.transaction {
          pushNotificationTweetsRepository.update(id)
        }
    })
  }

  def sendMessage(id: MessageId): Future[Unit] = {
    pushNotificationMessagesRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        val list = createContentSeq(notifications)
        sendContentSeq(list).map({ result =>
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
          _ <- sendContentSeq(createContentSeq(notifications))
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
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(pushNotificationFriendRequestsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          pushNotificationFriendRequestsRepository.update(id)
        }
    })
  }

  def sendInvitation(id: InvitationId): Future[Unit] = {
    pushNotificationInvitationsRepository.find(id).flatMap(_ match {
      case Some(notifications) =>
        for {
          _ <- sendContentSeq(createContentSeq(notifications))
          r <- db.transaction(pushNotificationInvitationsRepository.update(id))
        } yield (r)
      case None =>
        db.transaction {
          pushNotificationInvitationsRepository.update(id)
        }
    })
  }



  private def createContentSeq(notifications: Seq[PushNotification]): Seq[(OneSignalNotification, Seq[UserId])] = {
    notifications.flatMap({ notification =>
      val displayName = notification.displayName
      val message = notification.message
      val en = messageService.getPushNotificationMessage(notification.feedType, Seq(Locale.US), displayName, message)
      val jp = messageService.getPushNotificationMessage(notification.feedType, Seq(Locale.JAPAN), displayName, message)
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

