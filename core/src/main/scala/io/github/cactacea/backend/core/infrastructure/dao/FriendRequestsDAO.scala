package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendRequestsDAO @Inject()(db: DatabaseService, relationshipsDAO: RelationshipsDAO) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- relationshipsDAO.createRequestInProgress(accountId, sessionId)
      r <- createFriendsRequest(accountId, sessionId)
    } yield (r)
  }

  private def createFriendsRequest(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    val requestedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.notified        -> false,
          _.requestedAt     -> lift(requestedAt)
        ).returning(_.id)
    }
    run(q)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- relationshipsDAO.deleteRequestInProgress(accountId, sessionId)
      r <- deleteFriendRequests(accountId, sessionId)
    } yield (r)
  }

  private def deleteFriendRequests(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(id: FriendRequestId, accountId: AccountId): Future[Option[AccountId]] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
        .filter(_.accountId   == lift(accountId))
        .map(_.by)
    }
    run(q).map(_.headOption)
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
        .sortBy({ case (f, _, _) => f.id})(Ord.desc)
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
        .sortBy({ case (f, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, a, r) => FriendRequest(f, a, r, f.id.value)}))

  }

}
