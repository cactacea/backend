package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Relationships}
import io.github.cactacea.core.infrastructure.db.DatabaseService

@Singleton
class FollowsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    (for {
      _ <- _updateFollowCount(sessionId)
      r <- _updateFollow(accountId, sessionId)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertFollow(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _updateUnFollowCount(sessionId)
      r <- _updateUnFollow(accountId, sessionId)
    } yield (r)
  }

  private def _updateFollowCount(sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followCount -> (a.followCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFollowCount(sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followCount -> (a.followCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _insertFollow(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val followedAt = System.nanoTime()
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.followed        -> lift(true),
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFollow(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val followedAt = System.nanoTime()
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.followed        -> lift(true),
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFollow(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.followed          -> lift(false)
        )
    }
    run(q).map(_ == 1)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by           == lift(by))
        .filter(_.followed     == lift(true))
        .size
    }
    run(q).map(_ == 1)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) = {

    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(r => r.by == lift(accountId) && r.followed  == true && r.followedAt < lift(s))
        .sortBy(_.followedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
    }
    run(q).map(_.map({ case ((f, a), r) => (a.copy(position = f.followedAt), r)}))

  }

}