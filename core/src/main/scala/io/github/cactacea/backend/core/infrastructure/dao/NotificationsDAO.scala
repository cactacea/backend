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
                                 messageService: MessageService,
                                 ) {

  import db._

  def create(id: InvitationId, userId: UserId, sessionId: SessionId): Future[NotificationId] = {
    val by = sessionId.userId
    val url = deepLinkService.getInvitation(id)
    insert(userId, by, NotificationType.invitation, id.value, url)
  }

  def create(id: FriendRequestId, userId: UserId, sessionId: SessionId): Future[NotificationId] = {
    val by = sessionId.userId
    val url = deepLinkService.getRequest(id)
    insert(userId, by, NotificationType.friendRequest, id.value, url)
  }

  def create(feedId: FeedId, commentId: CommentId, userId: UserId, commentReply: Boolean, sessionId: SessionId): Future[NotificationId] = {
    val by = sessionId.userId
    val notificationType = commentReply match {
      case true => NotificationType.commentReply
      case false => NotificationType.feedReply
    }
    val url = deepLinkService.getComment(feedId, commentId)
    insert(userId, by, notificationType, commentId.value, url)
  }

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val url = deepLinkService.getFeed(feedId)
    val q = quote {
      infix"""
        insert into notifications (user_id, `by`, content_id, notification_type, url, unread, notified_at)
        select r.`by`, r.user_id, ${lift(feedId)}, ${lift(NotificationType.feed.toValue)}, ${lift(url)}, true as unread, CURRENT_TIMESTAMP
        from relationships r, feeds f
        where f.id = ${lift(feedId)}
        and r.user_id = ${lift(by)}
        and (
           (r.follow = true and (f.privacy_type in (0, 1)))
        or (r.is_friend = true and (f.privacy_type in (0, 1, 2)))
            )
        and r.muting = 0
        """.as[Action[Long]]
    }
    run(q).map(_ => ())
  }


  private def insert(userId: UserId, by: UserId, notificationType: NotificationType, contentId: Long, url: String): Future[NotificationId] = {
    val notifiedAt = System.currentTimeMillis()
    val contentIdOpt: Option[Long] = Some(contentId)
    val q = quote {
      query[Notifications].insert(
        _.userId         -> lift(userId),
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

  def updateReadStatus(notificationIds: List[NotificationId], sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[Notifications]
        .filter(_.userId == lift(userId))
        .filter(n => liftQuery(notificationIds).contains(n.id))
        .update(_.unread -> false)
    }
    run(q).map(_ => ())
  }

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           locales: Seq[Locale],
           sessionId: SessionId): Future[List[Notification]] = {

    val by = sessionId.userId
    val q = quote {
      (for {
        n <- query[Notifications]
          .filter(n => n.userId == lift(by))
          .filter(n => lift(since).forall(n.id < _))
          .filter(n => query[Blocks].filter(b => b.userId == lift(by) && b.by == n.by).isEmpty)
        a <- query[Users]
            .join(_.id == n.by)
        r <- query[Relationships]
            .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (n, a, r))
        .sortBy({ case (n, _, _) => n.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))

    }
    run(q).map(_.map({ case (n, a, r) =>
        val displayName = r.map(_.displayName).getOrElse(a.userName)
        val message = messageService.getNotificationMessage(n.notificationType, locales, displayName)
        Notification(n, message, n.id.value)
      }))


  }

}
