package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendsDAO @Inject()(db: DatabaseService, relationshipsDAO: RelationshipsDAO) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- createFriends(accountId, sessionId)
      _ <- updateFriendCount(1L, sessionId)
      _ <- relationshipsDAO.updateFriendBlockCount(accountId, 1L, sessionId)
      _ <- relationshipsDAO.createFriend(accountId, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFriends(accountId, sessionId)
      _ <- updateFriendCount(-1L, sessionId)
      _ <- relationshipsDAO.updateFriendBlockCount(accountId, -1L, sessionId)
      _ <- relationshipsDAO.deleteFriend(accountId, sessionId)
    } yield (())
  }

  private def createFriends(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
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
    run(q).map(_ => ())
  }

  private def deleteFriends(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Friends]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Friends]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(accountName: Option[String],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[List[Account]] = {
    find(sessionId.toAccountId, accountName, since, offset, count, sessionId)
  }

  def find(accountId: AccountId,
           accountName: Option[String],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        f <- query[Friends]
          .filter(f => f.by == lift(accountId))
          .filter(f => lift(since).forall(f.id < _))
          .filter(f => query[Blocks].filter(b => b.accountId == lift(by) && b.by == f.accountId).isEmpty)
        a <- query[Accounts]
          .join(_.id == f.accountId)
          .filter(a => lift(accountName.map(_ + "%")).forall(a.accountName like _))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r, f.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

  private def updateFriendCount(plus: Long, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.friendCount -> (a.friendCount + lift(plus))
        )
    }
    run(q).map(_ => ())
  }



}
