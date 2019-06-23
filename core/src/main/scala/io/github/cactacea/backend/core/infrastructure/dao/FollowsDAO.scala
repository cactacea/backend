package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Follows, Relationships}

@Singleton
class FollowsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFollow(accountId, sessionId)
      _ <- insertRelationship(accountId, sessionId)
      _ <- updateAccount(1L, sessionId)
      _ <- updateFollowBlockCount(accountId, 1L, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFollow(accountId, sessionId)
      _ <- updateRelationship(accountId, sessionId)
      _ <- updateAccount(-1L, sessionId)
      _ <- updateFollowBlockCount(accountId, -1L, sessionId)
    } yield (())
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

  private def updateFollowBlockCount(accountId: AccountId, count: Long, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
            insert into relationships (account_id, `by`, follow_block_count)
            select account_id, `by`, cnt from (select ${lift(accountId)} as account_id, `by`, ${lift(count)} as cnt from blocks where account_id = ${lift(by)}) t
            on duplicate key update follow_block_count = follow_block_count + ${lift(count)};
          """.as[Action[Long]]
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
          _.follow       -> true
        ).onConflictUpdate((t, _) =>
          t.follow -> true)
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
    val followedAt = System.currentTimeMillis()
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

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId
    val q = quote {
      (for {
        f <- query[Follows]
          .filter(f => f.by == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Accounts]
          .join(a => a.id == f.accountId)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r, f.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
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
      (for {
        f <- query[Follows]
          .filter(f => f.by == lift(accountId))
          .filter(f => lift(since).forall(f.id < _))
          .filter(f => query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == f.by) || (b.accountId == f.by && b.by == lift(by))
          ).isEmpty)
        a <- query[Accounts]
          .join(a => a.id == f.accountId)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r, f.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

}
