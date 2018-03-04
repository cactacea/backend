package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.NotificationType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models.Notifications
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class NotificationsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(accountId: AccountId, notificationType: NotificationType, contentId: Long): Future[NotificationId] = {
    for {
      id <- identifiesDAO.create().map(NotificationId(_))
      _ <- _insertNotifications(id, accountId, notificationType, Some(contentId), None, None)
    } yield (id)
  }

  private def _insertNotifications(id: NotificationId, accountId: AccountId, notificationType: NotificationType, contentId: Option[Long], message: Option[String], url: Option[String]): Future[Long] = {
    val notifiedAt = System.nanoTime()
    val q = quote {
      query[Notifications].insert(
        _.id                -> lift(id),
        _.accountId         -> lift(accountId),
        _.notificationType  -> lift(notificationType),
        _.contentId         -> lift(contentId),
        _.message           -> lift(message),
        _.url               -> lift(url),
        _.unread            -> true,
        _.notifiedAt        -> lift(notifiedAt)
      )
    }
    run(q)
  }

  def updateUnread(notificationIds: List[NotificationId], sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Notifications]
        .filter(_.accountId == lift(accountId))
        .filter(n => liftQuery(notificationIds).contains(n.id))
        .update(_.unread -> lift(false))
    }
    run(q).map(_ == notificationIds.size)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Notifications]] = {
    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Notifications]
        .filter(_.accountId == lift(accountId))
        .filter(_.notifiedAt < lift(s))
        .sortBy(_.notifiedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)
  }

}
