package io.github.cactacea.backend.core.infrastructure.dao

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{DeepLinkService, MessageService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class NotificationsDAO @Inject()(db: DatabaseService,
                                 deepLinkService: DeepLinkService,
                                 notificationMessagesService: MessageService,
                                 ) {

  import db._

//  def create(accountIds: List[AccountId], by: AccountId, notificationType: NotificationType, contentId: Long, url: String): Future[List[NotificationId]] = {
//    for {
//      ids <- insertNotifications(accountIds, by, notificationType, Some(contentId), url)
//    } yield (ids)
//  }
//
//  private def insertNotifications(ids: List[AccountId],
//                                  by: AccountId,
//                                  notificationType: NotificationType,
//                                  contentId: Option[Long], url: String): Future[List[NotificationId]] = {
//
//    val notifiedAt = System.currentTimeMillis()
//    val n = ids.map(id => Notifications(NotificationId(0L), id, by, notificationType, contentId, url, true,notifiedAt))
//    val q = quote {
//      liftQuery(n).foreach(e => query[Notifications].insert(e).returning(_.id))
//    }
//    run(q)
//  }

  private def insert(accountId: AccountId, by: AccountId, notificationType: NotificationType, contentId: Long, url: String): Future[NotificationId] = {
    val notifiedAt = System.currentTimeMillis()
    val contentIdOpt: Option[Long] = Some(contentId)
    val q = quote {
      query[Notifications].insert(
        _.accountId         -> lift(accountId),
        _.by                -> lift(by),
        _.notificationType  -> lift(notificationType),
        _.contentId         -> lift(contentIdOpt),
        _.url               -> lift(url),
        _.unread            -> true,
        _.notifiedAt        -> lift(notifiedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def createGroupInvitation(id: GroupInvitationId, accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val url = deepLinkService.getInvitation(id)
    insert(accountId, by, NotificationType.groupInvitation, id.value, url).flatMap(_ => Future.Done)
  }

  def createNotification(id: FriendRequestId, accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val url = deepLinkService.getRequest(id)
    insert(accountId, by, NotificationType.friendRequest, id.value, url).flatMap(_ => Future.Done)
  }

  def createComment(feedId: FeedId, commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    findByCommentId(commentId).flatMap({
      case Some((accountId, replied)) =>
        val by = sessionId.toAccountId
        val notificationType = replied match {
          case true => NotificationType.commentReply
          case false => NotificationType.feedReply
        }
        val url = deepLinkService.getComment(feedId, commentId)
        insert(accountId, by, notificationType, commentId.value, url).flatMap(_ => Future.Done)
      case None =>
        Future.Done
    })
  }

  private def findByCommentId(commentId: CommentId): Future[Option[(AccountId, Boolean)]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .join(query[Feeds]).on((c, f) => c.feedId == f.id)
        .map({ case (c, f) => (f.by, c.replyId.isDefined) })
    }
    run(q).map(_.headOption)
  }



  def createFeed(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val url = deepLinkService.getFeed(feedId)
    val q = quote {
      infix"""
        insert into notifications (account_id, `by`, notification_type, content_id, url, unread, notified_at)
        select r.`by`, r.account_id, ${lift(NotificationType.feed.toValue)}, ${lift(feedId)}, ${lift(url)}, false as unread, CURRENT_TIMESTAMP
        from relationships r, feeds f
        where f.id = ${lift(feedId)}
        and r.account_id = ${lift(by)}
        and (
           (r.is_follower = true and (f.privacy_type in (0, 1)))
        or (r.is_friend = true and (f.privacy_type in (0, 1, 2)))
            )
        """.as[Action[Long]]
    }
    run(q).map(_ => Unit)
  }

  def updateUnread(notificationIds: List[NotificationId], sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Notifications]
        .filter(_.accountId == lift(accountId))
        .filter(n => liftQuery(notificationIds).contains(n.id))
        .update(_.unread -> false)
    }
    run(q).map(_ => Unit)
  }

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           locales: Seq[Locale],
           sessionId: SessionId): Future[List[Notification]] = {

    val by = sessionId.toAccountId
    val q = quote {
      query[Notifications]
        .filter(n => n.accountId == lift(by))
        .filter(n => lift(since).forall(n.id < _))
        .filter(n => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == n.by) || (b.accountId == n.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Accounts]).on((c, a) => a.id == c.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((n, a), r) => (n, a, r)})
        .sortBy({ case (n, _, _) => n.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (n, a, r) =>
        val displayName = r.map(_.displayName).getOrElse(a.accountName)
        val message = notificationMessagesService.getNotificationMessage(n.notificationType, locales, displayName)
        Notification(n, message, n.id.value)
      }))


  }

}
