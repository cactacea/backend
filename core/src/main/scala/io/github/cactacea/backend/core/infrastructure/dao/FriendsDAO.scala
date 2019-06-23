package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FriendsSortType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFriend(accountId, sessionId)
      _ <- updateAccount(1L, sessionId)
      _ <- updateFriendBlockCount(accountId, 1L, sessionId)
      _ <- insertRelationship(accountId, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFriend(accountId, sessionId)
      _ <- updateAccount(-1L, sessionId)
      _ <- updateFriendBlockCount(accountId, -1L, sessionId)
      _ <- updateRelationship(accountId, friend = false, sessionId)
    } yield (())
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

  private def updateFriendBlockCount(accountId: AccountId, count: Long, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
             insert into relationships (account_id, `by`, friend_block_count)
             select account_id, `by`, cnt from (select ${lift(accountId)} as account_id, `by`, ${lift(count)} as cnt from blocks where account_id = ${lift(by)}) t
             on duplicate key update friend_block_count = friend_block_count + ${lift(count)};
          """.as[Action[Long]]
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
          _.isFriend            -> true
        ).onConflictUpdate((t, _) => t.isFriend -> true)
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
          _.isFriend          -> lift(friend)
        )
    }
    run(q).map(_ => Unit)
  }

  private def insertFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val friendsAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Friends]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.friendedAt      -> lift(friendsAt)
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

  def find(since: Option[Long], offset: Int, count: Int, sortType: FriendsSortType, sessionId: SessionId): Future[List[Account]] = {
    if (sortType == FriendsSortType.accountName) {
      for {
        n <- findAccountName(since)
        r <- findSortByAccountName(n, offset, count, sessionId)
      } yield (r)
    } else {
      findSortById(since, offset, count, sessionId)
    }
  }

  private def findAccountName(since: Option[Long]): Future[Option[String]] = {
    since match {
      case Some(id) =>
        val accountId = AccountId(id)
        val q = quote {
          query[Accounts]
            .filter(_.id == lift(accountId))
            .map(_.accountName)
        }
        run(q).map(_.headOption)
      case None =>
        Future.None
    }
  }

  private def findSortByAccountName(accountName: Option[String], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        f <- query[Friends]
            .filter(_.by == lift(by))
        a <- query[Accounts]
            .join(a => a.id == f.accountId && lift(accountName).forall(a.accountName gt _))
        r <- query[Relationships]
            .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r, a.id))
        .sortBy({ case (a, _, _) => a.accountName})(Ord.asc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

  private def findSortById(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        f <- query[Friends]
          .filter(f => f.by == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Accounts]
            .join(_.id == f.accountId)
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
      query[Friends]
        .filter(f => f.by == lift(accountId))
        .filter(f => lift(since).forall(f.id < _ ))
        .filter(f => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == f.by) || (b.accountId == f.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f.id)})
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }



}
