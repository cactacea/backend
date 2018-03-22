package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class FriendRequestsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _
  @Inject private var timeService: TimeService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      id <- identifyService.generate().map(FriendRequestId(_))
      _ <- insert(id, accountId, sessionId)
    } yield (id)
  }

  private def insert(id: FriendRequestId, accountId: AccountId, sessionId: SessionId): Future[Long] = {
    val requestedAt = timeService.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .insert(
          _.id              -> lift(id),
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.notified        -> false,
          _.requestStatus   -> lift(FriendRequestStatusType.noResponded),
          _.requestedAt     -> lift(requestedAt)
        )
    }
    run(q)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .filter(_.requestStatus == lift(FriendRequestStatusType.noResponded))
        .delete
    }
    run(q).map(_ == 1)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .take(lift(1))
        .size
    }
    run(q).map(_ == 1)
  }

  def find(id: FriendRequestId): Future[Option[FriendRequests]] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
    }
    run(q).map(_.headOption)
  }

  def find(id: FriendRequestId, sessionId: SessionId): Future[Option[FriendRequests]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
        .filter(_.accountId   == lift(accountId))
    }
    run(q).map(_.headOption)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], received: Boolean, sessionId: SessionId): Future[List[(FriendRequests, Accounts, Option[Relationships])]] = {
    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    if (received) {
      val q = quote {
        query[FriendRequests].filter(f => f.accountId == lift(by) && (infix"f.id < ${lift(s)}".as[Boolean] || lift(s) == -1L))
          .join(query[Accounts]).on((f, a) => a.id == f.by)
          .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
          .map({ case ((f, a), r) => (f, a, r)})
          .sortBy(_._1.id)(Ord.descNullsLast)
          .drop(lift(o))
          .take(lift(c))
      }
      run(q)

    } else {
      val q = quote {
        query[FriendRequests].filter(f => f.by == lift(by) && f.requestedAt < lift(s))
          .join(query[Accounts]).on((f, a) => a.id == f.by)
          .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
          .map({ case ((f, a), r) => (f, a, r)})
          .sortBy(_._1.id)(Ord.descNullsLast)
          .drop(lift(o))
          .take(lift(c))
      }
      run(q)

    }

  }

  def update(id: FriendRequestId, status: FriendRequestStatusType, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id            == lift(id))
        .filter(_.accountId     == lift(accountId))
        .update(_.requestStatus -> lift(status))
    }
    run(q).map(_ == 1)

  }

  def updateNotified(id: FriendRequestId, notified: Boolean = true): Future[Boolean] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id == lift(id))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ == 1)
  }

}
