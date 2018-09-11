package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendRequestsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    val requestedAt = timeService.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.notified        -> false,
          _.requestStatus   -> lift(FriendRequestStatusType.noResponded),
          _.requestedAt     -> lift(requestedAt)
        ).returning(_.id)
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
        .nonEmpty
    }
    run(q)
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
        query[FriendRequests].filter(f => f.accountId == lift(by) && (f.id < lift(s) || lift(s) == -1L))
          .join(query[Accounts]).on((f, a) => a.id == f.by)
          .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
          .map({ case ((f, a), r) => (f, a, r)})
          .sortBy(_._1.id)(Ord.desc)
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
          .sortBy(_._1.id)(Ord.desc)
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
