package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class FollowersDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    (for {
      r <- _updateFollower(accountId, sessionId)
      _ <- _updateFollowerCount(accountId)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.value(Unit)
      case false =>
        _insertFollower(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    for {
      r <- _updateUnFollower(accountId, sessionId)
      _ <- _updateUnFollowerCount(accountId)
    } yield (r)
  }

  private def _insertFollower(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val beFollowedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.follower          -> true,
          _.beingFollowedAt      -> lift(beFollowedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  private def _updateFollowerCount(accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followerCount -> (a.followerCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFollowerCount(accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followerCount -> (a.followerCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFollower(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val beFollowedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.follower        -> true,
            _.beingFollowedAt  -> lift(beFollowedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFollower(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.follower        -> false
        )
    }
    run(q).map(_ == 1)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Relationships)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId


    val q = quote {
      query[Relationships].filter(f => f.accountId == lift(by) && f.follow  == true && (f.followedAt < lift(s) || lift(s) == -1L) )
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(p => (p._3.beingFollowedAt, p._1.id))(Ord(Ord.desc, Ord.desc))
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
      query[Relationships].filter(f => f.accountId == lift(accountId) && f.follow  == true && (f.followedAt < lift(s) || lift(s) == -1L) )
        .filter(r => query[Blocks]
          .filter(_.accountId == r.accountId)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == true || b.beingBlocked == true)
          .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(p => (p._3.beingFollowedAt, p._1.id))(Ord(Ord.desc, Ord.desc))
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)
  }


}
