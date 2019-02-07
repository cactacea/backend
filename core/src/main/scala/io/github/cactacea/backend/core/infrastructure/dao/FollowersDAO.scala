package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FollowersDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFollower(accountId, sessionId)
      _ <- insertRelationship(accountId, sessionId)
      _ <- updateAccount(accountId, 1L)
      _ <- updateFollowerBlockCount(accountId, 1L, sessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFollower(accountId, sessionId)
      _ <- updateRelationship(accountId, sessionId)
      _ <- updateAccount(accountId, -1L)
      _ <- updateFollowerBlockCount(accountId, -1L, sessionId)
    } yield (Unit)
  }

  private def insertRelationship(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.isFollower          -> true
        ).onConflictUpdate((t, _) =>
          t.isFollower -> true)
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
          _.isFollower        -> false
        )
    }
    run(q).map(_ => Unit)
  }

  private def updateFollowerBlockCount(accountId: AccountId, count: Long, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
             insert into relationships (account_id, `by`, follower_block_count)
             select account_id, `by`, cnt from (select ${lift(accountId)} as account_id, `by`, ${lift(count)} as cnt from blocks where account_id = ${lift(by)}) t
             on duplicate key update follower_block_count = follower_block_count + ${lift(count)};
          """.as[Action[Long]]
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
    val followedAt = System.currentTimeMillis()
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

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Followers]
        .filter(f => f.accountId == lift(by))
        .filter(f => lift(since).forall(f.id < _))
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f.id)})
        .sortBy({ case (_, _, id) => id })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

  def find(accountId: AccountId,
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Followers]
        .filter(f => f.accountId == lift(accountId))
        .filter(f => lift(since).forall(f.id < _))
        .filter(f => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == f.by) || (b.accountId == f.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f.id)})
        .sortBy({ case (_, _, id) => id })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))
  }


}
