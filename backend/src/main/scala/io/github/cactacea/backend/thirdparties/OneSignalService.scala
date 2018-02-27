package io.github.cactacea.backend.thirdparties

import com.google.inject.{Inject, Singleton}
import com.twitter.util.{Future, Return, Throw}
import io.github.cactacea.core.infrastructure.clients.onesignal.{OneSignalHttpClient, OneSignalNotificationFactory}
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.{DatabaseService, PushNotificationService}

@Singleton
class OneSignalService @Inject()(db: DatabaseService) extends PushNotificationService {

  @Inject var oneSignalHttpClient: OneSignalHttpClient = _

  override def notifyGroupInvite(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], groupId: GroupId, invitedAt: Long): Future[List[AccountId]] = {
    Future.traverseSequentially(tokens.grouped(2000).toSeq) { t =>
      val tokens2000 = t.map(_._1)
      val accountIds2000 = t.map(_._2)
      val notification = OneSignalNotificationFactory.createGroupInvite(accountId, displayName, tokens2000, groupId, invitedAt)
      oneSignalHttpClient.post("/notifications", notification).transform {
        case Return(r) =>
          if (r.statusCode >= 200 && r.statusCode <= 299) {
            Future.value(accountIds2000)
          } else {
            Future.value(Seq[AccountId]())
          }
        case Throw(_) =>
          Future.value(Seq[AccountId]())
      }
    }.map(_.flatten.toList)
  }

  override def notifyComment(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], commentId: CommentId, postedAt: Long): Future[List[AccountId]] = {
    Future.traverseSequentially(tokens.grouped(2000).toSeq) { t =>
      val tokens2000 = t.map(_._1)
      val accountIds2000 = t.map(_._2)
      val notification = OneSignalNotificationFactory.createComment(accountId, displayName, tokens2000, commentId, postedAt)
      oneSignalHttpClient.post("/notifications", notification).transform {
        case Return(r) =>
          if (r.statusCode >= 200 && r.statusCode <= 299) {
            Future.value(accountIds2000)
          } else {
            Future.value(Seq[AccountId]())
          }
        case Throw(_) =>
          Future.value(Seq[AccountId]())
      }
    }.map(_.flatten.toList)
  }

  override def notifyMessage(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], messageId: MessageId, message: Option[String], mediumId: Option[MediumId], postedAt: Long, show: Boolean): Future[List[AccountId]] = {
    Future.traverseSequentially(tokens.grouped(2000).toSeq) { t =>
      val tokens2000 = t.map(_._1)
      val accountIds2000 = t.map(_._2)
      val notification = OneSignalNotificationFactory.createMessage(accountId, displayName, tokens2000, messageId, message, mediumId, postedAt, show)
      oneSignalHttpClient.post("/notifications", notification).transform {
        case Return(r) =>
          if (r.statusCode >= 200 && r.statusCode <= 299) {
            Future.value(accountIds2000)
          } else {
            Future.value(Seq[AccountId]())
          }
        case Throw(_) =>
          Future.value(Seq[AccountId]())
      }
    }.map(_.flatten.toList)
  }

  override def notifyFeed(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], feedId: FeedId, postedAt: Long): Future[List[AccountId]] = {
    Future.traverseSequentially(tokens.grouped(2000).toSeq) { t =>
      val tokens2000 = t.map(_._1)
      val accountIds2000 = t.map(_._2)
      val notification = OneSignalNotificationFactory.createFeed(accountId, displayName, tokens2000, feedId, postedAt)
      oneSignalHttpClient.post("/notifications", notification).transform {
        case Return(r) =>
          if (r.statusCode >= 200 && r.statusCode <= 299) {
            Future.value(accountIds2000)
          } else {
            Future.value(Seq[AccountId]())
          }
        case Throw(_) =>
          Future.value(Seq[AccountId]())
      }
    }.map(_.flatten.toList)
  }


}

