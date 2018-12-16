package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Followings, Relationships}

@Singleton
class FollowingsDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFollowing(accountId, sessionId)
      _ <- insertRelationship(accountId, sessionId)
      _ <- updateAccount(1L, sessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFollowing(accountId, sessionId)
      _ <- updateRelationship(accountId, sessionId)
      _ <- updateAccount(-1L, sessionId)
    } yield (Unit)
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
          _.following          -> true
        ).onConflictUpdate((t, _) => t.following -> true)
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
          _.following          -> false
        )
    }
    run(q).map(_ => Unit)
  }

  private def insertFollowing(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val followedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Followings]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  private def deleteFollowing(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Followings]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Followings]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by           == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId
    val q = quote {
      query[Followings]
        .filter(f => f.by == lift(by))
        .filter(f => lift(since).forall(f.id < _))
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f.id)})
        .sortBy({ case (_, _, id) => id} )(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

  def findAll(accountId: AccountId,
              since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Followings]
        .filter(f => f.by == lift(accountId))
        .filter(f => lift(since).forall(f.id < _))
        .filter(f => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == f.by) || (b.accountId == f.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f.id)})
        .sortBy({ case (_, _, id) => id} )(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

}
