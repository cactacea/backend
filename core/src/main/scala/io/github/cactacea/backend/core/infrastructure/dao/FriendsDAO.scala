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
    (for {
      _ <- _insertFriend(accountId, sessionId)
      _ <- _updateAccount(1L, sessionId)
      r <- _updateRelationship(accountId, true, sessionId)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        _insertRelationship(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- _deleteFriend(accountId, sessionId)
      r <- _updateAccount(-1L, sessionId)
      _ <- _updateRelationship(accountId, false, sessionId)
    } yield (r)
  }

  private def _updateAccount(count: Long, sessionId: SessionId): Future[Unit] = {
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

  private def _insertRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.friend            -> true
        )
    }
    run(q).map(_ => Unit)
  }

  private def _updateRelationship(accountId: AccountId, friend: Boolean, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.friend          -> lift(friend)
        )
    }
    run(q).map(_ == 1)
  }

  private def _insertFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
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

  private def _deleteFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
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

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Friends)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Friends]
        .filter(f => f.by == lift(by))
        .filter(f => f.id < lift(s) || lift(s) == -1L)
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Friends)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Friends]
        .filter(f => f.by == lift(accountId) && (f.id < lift(s) || lift(s) == -1L) )
        .filter(f => query[Blocks]
          .filter(_.accountId == f.accountId)
          .filter(_.by        == lift(by))
          .isEmpty)
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

}
