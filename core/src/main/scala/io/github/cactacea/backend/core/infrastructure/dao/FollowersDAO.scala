package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FollowersDAO @Inject()(db: DatabaseService, relationshipsDAO: RelationshipsDAO) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFollowers(accountId, sessionId)
      _ <- updateFollowerCount(1L, sessionId)
      _ <- relationshipsDAO.createFollower(accountId, sessionId)
      _ <- relationshipsDAO.updateFollowerBlockCount(accountId, 1L, sessionId)
    } yield (())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFollowers(accountId, sessionId)
      _ <- updateFollowerCount(-1L, sessionId)
      _ <- relationshipsDAO.deleteFollower(accountId, sessionId)
      _ <- relationshipsDAO.updateFollowerBlockCount(accountId, -1L, sessionId)
    } yield (())
  }

  private def insertFollowers(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
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
    run(q).map(_ => ())
  }

  private def deleteFollowers(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Followers]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => ())
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
        f <- query[Followers]
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

  private def updateFollowerCount(plus: Long, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followerCount -> (a.followerCount + lift(plus))
        )
    }
    run(q).map(_ => ())
  }


}
