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
    for {
      _ <- insertFollower(accountId, sessionId)
      r <- insertRelationship(accountId, sessionId)
      _ <- updateAccount(accountId, 1L)
    } yield (r)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFollower(accountId, sessionId)
      r <- updateRelationship(accountId, sessionId)
      _ <- updateAccount(accountId, -1L)
    } yield (r)
  }

  private def insertRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.follower          -> true
        ).onConflictUpdate((t, _) => t.follower -> true)
    }
    run(q).map(_ => Unit)
  }

  private def updateRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.follower        -> false
        )
    }
    run(q).map(_ => Unit)
  }

  private def updateAccount(accountId: AccountId, followerCount: Long): Future[Unit] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followerCount -> (a.followerCount + lift(followerCount))
        )
    }
    run(q).map(_ => Unit)
  }

  private def insertFollower(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
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

  private def deleteFollower(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Followers]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def findAll(since: Option[Long],
              offset: Option[Int],
              count: Option[Int],
              sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Followers)]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Followers].filter(f => f.accountId == lift(by) && lift(since).forall(s => f.id < s))
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(offset).getOrElse(0))
        .take(lift(count).getOrElse(20))
    }
    run(q)

  }

  def findAll(accountId: AccountId,
              since: Option[Long],
              offset: Option[Int],
              count: Option[Int],
              sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Followers)]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Followers].filter(f => f.accountId == lift(accountId) && lift(since).forall(s => f.id < s))
        .filter(r => query[Blocks]
          .filter(_.accountId == lift(by))
          .filter(_.by        == r.accountId)
          .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(offset).getOrElse(0))
        .take(lift(count).getOrElse(20))
    }
    run(q)
  }


}
