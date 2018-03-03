package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FriendRequestsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      id <- identifiesDAO.create().map(FriendRequestId(_))
      _ <- insert(id, accountId, sessionId)
    } yield (id)
  }

  private def insert(id: FriendRequestId, accountId: AccountId, sessionId: SessionId): Future[Long] = {
    val requestedAt = System.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .insert(
          _.id              -> lift(id),
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.requestStatus   -> lift(FriendRequestStatusType.noresponsed.toValue),
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
        .filter(_.requestStatus == lift(FriendRequestStatusType.noresponsed.toValue))
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

  def find(friendRequestId: FriendRequestId, sessionId: SessionId): Future[Option[FriendRequests]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(friendRequestId))
        .filter(_.accountId   == lift(accountId))
    }
    run(q).map(_.headOption)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], received: Boolean, sessionId: SessionId): Future[List[(FriendRequests, Accounts, Option[Relationships])]] = {
    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    if (received) {
      val q = quote {
        query[FriendRequests].filter(f => f.accountId == lift(by) && f.requestedAt < lift(s))
          .sortBy(_.requestedAt)(Ord.descNullsLast)
          .drop(lift(o))
          .take(lift(c))
          .join(query[Accounts]).on((f, a) => a.id == f.by)
          .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
      }

      run(q).map(_.map({ case ((f, a), r) => (f, a.copy(position = f.requestedAt), r)}))

    } else {
      val q = quote {
        query[FriendRequests].filter(f => f.by == lift(by) && f.requestedAt < lift(s))
          .sortBy(_.requestedAt)(Ord.descNullsLast)
          .drop(lift(o))
          .take(lift(c))
          .join(query[Accounts]).on((f, a) => a.id == f.by)
          .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
      }

      run(q).map(_.map({ case ((f, a), r) => (f, a.copy(position = f.requestedAt), r)}))

    }

  }

  def update(friendRequestId: FriendRequestId, friendRequestStatus: FriendRequestStatusType, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id      == lift(friendRequestId))
        .filter(_.accountId  == lift(accountId))
        .update(_.requestStatus -> lift(friendRequestStatus.toValue))
    }
    run(q).map(_ == 1)

  }

}
