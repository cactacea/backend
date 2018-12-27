package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.FriendsSortType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyFriend, AccountNotFriend}

@Singleton
class FriendsDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFriend(accountId, sessionId)
      _ <- updateAccount(1L, sessionId)
      _ <- insertRelationship(accountId, sessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFriend(accountId, sessionId)
      _ <- updateAccount(-1L, sessionId)
      _ <- updateRelationship(accountId, friend = false, sessionId)
    } yield (Unit)
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
    val friendsAt = timeService.currentTimeMillis()
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
      query[Friends]
        .filter(f => f.by == lift(by))
        .join(query[Accounts]).on((f, a) => a.id == f.accountId && lift(accountName).forall(a.accountName gt _))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((_, a), r) => (a, r, a.id)})
        .sortBy({ case (a, _, _) => a.accountName})(Ord.asc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

  private def findSortById(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[Friends]
        .filter(f => f.by == lift(by))
        .filter(f => lift(since).forall(f.id < _))
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f.id)})
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


  // Validators

  def validateNotExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFriend))
      case false =>
        Future.Unit
    })
  }

  def validateExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(AccountNotFriend))
      case true =>
        Future.Unit
    })
  }

}
