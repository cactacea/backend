package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendsDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFriend(accountId, sessionId)
      _ <- updateAccount(1L, sessionId)
      r <- insertRelationship(accountId, sessionId)
    } yield (r)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFriend(accountId, sessionId)
      r <- updateAccount(-1L, sessionId)
      _ <- updateRelationship(accountId, friend = false, sessionId)
    } yield (r)
  }

  private def updateAccount(count: Long, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.friendCount -> (a.friendCount + lift(count))
        )
    }
    run(q).map(_ => Unit)
  }

  private def insertRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.friend            -> true
        ).onConflictUpdate((t, _) => t.friend -> true)
    }
    run(q).map(_ => Unit)
  }

  private def updateRelationship(accountId: AccountId, friend: Boolean, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.friend          -> lift(friend)
        )
    }
    run(q).map(_ => Unit)
  }

  private def insertFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val friendedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Friends]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.friendedAt      -> lift(friendedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  private def deleteFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Friends]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Friends]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Friends)]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Friends]
        .filter(f => f.by == lift(by))
        .filter(f => lift(since).forall(f.friendedAt < _))
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy({ case (_, _, f) => f.friendedAt})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q)

  }

  def findAll(accountId: AccountId,
              since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Friends)]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Friends]
        .filter(f => f.by == lift(accountId))
        .filter(f => lift(since).forall(f.friendedAt < _ ))
        .filter(f => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == f.by) || (b.accountId == f.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy({ case (_, _, f) => f.friendedAt})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q)

  }

}
