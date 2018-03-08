package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.domain.enums.NotificationType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models.Notifications

@Singleton
class NotificationsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _
  @Inject private var timeService: TimeService = _

  def create(accountIds: List[AccountId], notificationType: NotificationType, contentId: Long, url: String): Future[List[NotificationId]] = {
    for {
      a <- Future.traverseSequentially(accountIds) { accountId => identifyService.generate().map({id => (NotificationId(id), accountId) } ) }
      _ <- _insertNotifications(a.toList, notificationType, Some(contentId), None, Some(url))
      ids = a.map(_._1).toList
    } yield (ids)
  }

  private def _insertNotifications(ids: List[(NotificationId, AccountId)], notificationType: NotificationType, contentId: Option[Long], message: Option[String], url: Option[String]): Future[List[Long]] = {
    val notifiedAt = timeService.nanoTime()
    val n = ids.map({ case (id, accountId) =>
      Notifications(
        id,
        accountId,
        notificationType,
        contentId,
        message,
        url,
        true,
        notifiedAt
      ) })
    val q = quote {
      liftQuery(n).foreach(e => query[Notifications].insert(e))
    }
    run(q)
  }

  def create(accountId: AccountId, notificationType: NotificationType, contentId: Long, url: String): Future[NotificationId] = {
    for {
      id <- identifyService.generate().map(NotificationId(_))
      _ <- _insertNotifications(id, accountId, notificationType, Some(contentId), None, Some(url))
    } yield (id)
  }

  private def _insertNotifications(id: NotificationId, accountId: AccountId, notificationType: NotificationType, contentId: Option[Long], message: Option[String], url: Option[String]): Future[Long] = {
    val notifiedAt = timeService.nanoTime()
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
        .update(_.unread -> false)
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
