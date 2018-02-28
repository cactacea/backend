package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Relationships}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FriendsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    (for {
      _ <- _updateFriendCount(sessionId)
      r <- _updateFriend(accountId, sessionId)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertFriend(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _updateUnFriendCount(sessionId)
      _ <- _updateUnFriend(accountId, sessionId)
    } yield (true)
  }

  private def _updateFriendCount(sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.friendCount -> (a.friendCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFriendCount(sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.friendCount -> (a.friendCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _insertFriend(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val friendedAt = System.nanoTime()
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.friend          -> lift(true),
          _.friendedAt        -> lift(friendedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFriend(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val friendedAt = System.nanoTime()
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.friend          -> lift(true),
          _.friendedAt        -> lift(friendedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFriend(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.friend          -> lift(false)
        )
    }
    run(q).map(_ == 1)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .filter(_.friend    == lift(true))
        .size
    }
    run(q).map(_ == 1)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships])]] = {

    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(r => r.by == lift(accountId) && r.friend  == true && r.friendedAt < lift(s))
        .sortBy(_.friendedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
    }
    run(q).map(_.map({ case ((f, a), r) => (a.copy(position = f.friendedAt), r)}))

  }

}
