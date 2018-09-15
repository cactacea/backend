package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class FollowingDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    (for {
      r <- _updateFollow(accountId, sessionId)
      _ <- _updateFollowCount(sessionId)
    } yield (r)).map(_ match {
      case true =>
        Future.value(Unit)
      case false =>
        _insertFollow(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- _updateUnFollowCount(sessionId)
      r <- _updateUnFollow(accountId, sessionId)
    } yield (r)
  }

  private def _updateFollowCount(sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followCount -> (a.followCount + 1)
        )
    }
    run(q).map(_ => Unit)
  }

  private def _updateUnFollowCount(sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followCount -> (a.followCount - 1)
        )
    }
    run(q).map(_ => Unit)
  }

  private def _insertFollow(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val followedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.follow          -> true,
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  private def _updateFollow(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val followedAt: Long = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .update(
          _.follow          -> true,
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFollow(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.follow          -> false
        )
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by           == lift(by))
        .filter(_.follow       == true)
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Relationships)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(f => f.by == lift(by) && f.follow  == true && (f.followedAt < lift(s) || lift(s) == -1L) )
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(p => (p._3.followedAt, p._1.id))(Ord(Ord.desc, Ord.desc))
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Relationships)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(f => f.by == lift(accountId) && f.follow  == true && (f.followedAt < lift(s) || lift(s) == -1L) )
        .filter(r => query[Blocks]
          .filter(_.accountId == r.accountId)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == true || b.beingBlocked == true)
          .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(p => (p._3.followedAt, p._1.id))(Ord(Ord.desc, Ord.desc))
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

}