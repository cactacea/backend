package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendRequestsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    val requestedAt = System.currentTimeMillis()
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

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .filter(_.requestStatus == lift(FriendRequestStatusType.noResponded))
        .delete
    }
    run(q).map(_ => Unit)
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

  def find(id: FriendRequestId, sessionId: SessionId): Future[Option[AccountId]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
        .filter(_.accountId   == lift(accountId))
        .map(_.by)
    }
    run(q).map(_.headOption)
  }

  private def findReceivedRequests(since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[List[FriendRequest]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        f <- query[FriendRequests]
          .filter(_.accountId == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Accounts]
          .filter(_.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, a, r))
        .sortBy(_._1.id)(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, a, r) => FriendRequest(f, a, r, f.id.value)}))

  }

  private def findSentRequests(since: Option[Long],
                                   offset: Int,
                                   count: Int,
                                   sessionId: SessionId): Future[List[FriendRequest]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        f <- query[FriendRequests]
          .filter(_.by == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Accounts]
          .filter(_.id == f.accountId)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, a, r))
        .sortBy(_._1.id)(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, a, r) => FriendRequest(f, a, r, f.id.value)}))

  }


  def find(since: Option[Long],
           offset: Int,
           count: Int,
           received: Boolean,
           sessionId: SessionId): Future[List[FriendRequest]] = {


    if (received) {
      findReceivedRequests(since, offset, count, sessionId)
    } else {
      findSentRequests(since, offset, count, sessionId)
    }

  }

  def update(id: FriendRequestId, status: FriendRequestStatusType, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id            == lift(id))
        .filter(_.accountId     == lift(accountId))
        .update(_.requestStatus -> lift(status))
    }
    run(q).map(_ => Unit)

  }



}
