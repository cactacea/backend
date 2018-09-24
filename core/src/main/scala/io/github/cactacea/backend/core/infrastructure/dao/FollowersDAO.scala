package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FollowersDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    (for {
      _ <- _insertFollower(accountId, sessionId)
      r <- _updateRelationship(accountId, true, sessionId)
      _ <- _updateAccount(accountId, 1L)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.value(Unit)
      case false =>
        _insertRelationship(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _deleteFollower(accountId, sessionId)
      r <- _updateRelationship(accountId, false, sessionId)
      _ <- _updateAccount(accountId, -1L)
    } yield (r)
  }

  private def _insertRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.follower          -> true
        )
    }
    run(q).map(_ => Unit)
  }

  private def _updateRelationship(accountId: AccountId, follower: Boolean, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.follower        -> lift(follower)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateAccount(accountId: AccountId, followerCount: Long): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followerCount -> (a.followerCount + lift(followerCount))
        )
    }
    run(q).map(_ == 1)
  }

  private def _insertFollower(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val followedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Followers]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  private def _deleteFollower(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Followers]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Followers)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId


    val q = quote {
      query[Followers].filter(f => f.accountId == lift(by) && (f.id < lift(s) || lift(s) == -1L) )
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Followers)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Followers].filter(f => f.accountId == lift(accountId) && (f.id < lift(s) || lift(s) == -1L) )
        .filter(r => query[Blocks]
          .filter(_.accountId == lift(by))
          .filter(_.by        == r.accountId)
          .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)
  }


}
