package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class NotificationsDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountIds: List[AccountId], by: AccountId, notificationType: NotificationType, contentId: Long, url: String): Future[List[NotificationId]] = {
    for {
      ids <- _insertNotifications(accountIds, by, notificationType, Some(contentId), url)
    } yield (ids)
  }

  private def _insertNotifications(ids: List[AccountId], by: AccountId, notificationType: NotificationType, contentId: Option[Long], url: String): Future[List[NotificationId]] = {
    val notifiedAt = timeService.nanoTime()
    val n = ids.map(id => Notifications(NotificationId(0L), id, by, notificationType, contentId, url, true,notifiedAt))
    val q = quote {
      liftQuery(n).foreach(e => query[Notifications].insert(e).returning(_.id))
    }
    run(q)
  }

  def create(accountId: AccountId, by: AccountId, notificationType: NotificationType, contentId: Long, url: String): Future[NotificationId] = {
    val notifiedAt = timeService.nanoTime()
    val contentIdOpt: Option[Long] = Some(contentId)
    val q = quote {
      query[Notifications].insert(
        _.accountId         -> lift(accountId),
        _.notificationType  -> lift(notificationType),
        _.contentId         -> lift(contentIdOpt),
        _.url               -> lift(url),
        _.unread            -> true,
        _.notifiedAt        -> lift(notifiedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def updateUnread(notificationIds: List[NotificationId], sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Notifications]
        .filter(_.accountId == lift(accountId))
        .filter(n => liftQuery(notificationIds).contains(n.id))
        .update(_.unread -> false)
    }
    run(q).map(_ == notificationIds.size)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Notifications, Accounts, Option[Relationships])]] = {
    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Notifications].filter(n => n.accountId == lift(accountId) && (n.id < lift(s) || lift(s) == -1L) &&
        query[Blocks].filter(b => b.accountId == n.by && b.by == lift(accountId) && (b.blocked || b.beingBlocked)).isEmpty)
        .join(query[Accounts]).on((c, a) => a.id == c.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(accountId)})
        .map({ case ((n, a), r) => (n, a, r)})
        .sortBy(_._1.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)
  }

}
