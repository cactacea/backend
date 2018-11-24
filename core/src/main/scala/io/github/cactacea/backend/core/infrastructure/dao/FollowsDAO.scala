package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Follows, Relationships}

@Singleton
class FollowsDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFollow(accountId, sessionId)
      r <- insertRelationship(accountId, sessionId)
      _ <- updateAccount(1L, sessionId)
    } yield (r)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFollow(accountId, sessionId)
      _ <- updateRelationship(accountId, sessionId)
      r <- updateAccount(-1L, sessionId)
    } yield (r)
  }

  private def updateAccount(count: Long, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followCount -> (a.followCount + lift(count))
        )
    }
    run(q).map(_ => Unit)
  }

  private def insertRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.follow          -> true
        ).onConflictUpdate((t, _) => t.follow -> true)
    }
    run(q).map(_ => Unit)
  }

  private def updateRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .update(
          _.follow          -> false
        )
    }
    run(q).map(_ => Unit)
  }

  private def insertFollow(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val followedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Follows]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  private def deleteFollow(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Follows]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Follows]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by           == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Follows)]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Follows].filter(f => f.by == lift(by) && lift(since).forall(s => f.id < s))
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
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
              sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Follows)]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Follows].filter(f => f.by == lift(accountId) && lift(since).forall(s => f.id < s))
        .filter(r => query[Blocks]
          .filter(_.accountId == lift(by))
          .filter(_.by        == r.accountId)
          .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(offset).getOrElse(0))
        .take(lift(count).getOrElse(20))
    }
    run(q)

  }

}
