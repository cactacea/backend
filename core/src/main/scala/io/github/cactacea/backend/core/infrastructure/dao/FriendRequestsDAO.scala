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

  def create(userId: UserId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- relationshipsDAO.createRequestInProgress(userId, sessionId)
      r <- createFriendsRequest(userId, sessionId)
    } yield (r)
  }

  private def createFriendsRequest(userId: UserId, sessionId: SessionId): Future[FriendRequestId] = {
    val requestedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[FriendRequests]
        .insert(
          _.userId       -> lift(userId),
          _.by              -> lift(by),
          _.notified        -> false,
          _.requestedAt     -> lift(requestedAt)
        ).returning(_.id)
    }
    run(q)
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- relationshipsDAO.deleteRequestInProgress(userId, sessionId)
      r <- deleteFriendRequests(userId, sessionId)
    } yield (r)
  }

  private def deleteFriendRequests(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[FriendRequests]
        .filter(_.userId     == lift(userId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(userId: UserId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[FriendRequests]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(id: FriendRequestId, userId: UserId): Future[Option[UserId]] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
        .filter(_.userId   == lift(userId))
        .map(_.by)
    }
    run(q).map(_.headOption)
  }

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           received: Boolean,
           sessionId: SessionId): Future[Seq[FriendRequest]] = {


    if (received) {
      findReceivedRequests(since, offset, count, sessionId)
    } else {
      findSentRequests(since, offset, count, sessionId)
    }

  }

  private def findReceivedRequests(since: Option[Long],
                                   offset: Int,
                                   count: Int,
                                   sessionId: SessionId): Future[Seq[FriendRequest]] = {

    val by = sessionId.userId

    val q = quote {
      (for {
        f <- query[FriendRequests]
          .filter(_.userId == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Users]
          .filter(_.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
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
                               sessionId: SessionId): Future[Seq[FriendRequest]] = {

    val by = sessionId.userId

    val q = quote {
      (for {
        f <- query[FriendRequests]
          .filter(_.by == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Users]
          .filter(_.id == f.userId)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (f, a, r))
        .sortBy({ case (f, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, a, r) => FriendRequest(f, a, r, f.id.value)}))

  }

}
