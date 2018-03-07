package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Relationships}

@Singleton
class FollowersDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    (for {
      r <- _updateFollower(accountId, sessionId)
      _ <- _updateFollowerCount(accountId)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertFollower(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    for {
      r <- _updateUnFollower(accountId, sessionId)
      _ <- _updateUnFollowerCount(accountId)
    } yield (r)
  }

  private def _insertFollower(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.follower          -> true
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFollowerCount(accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followerCount -> (a.followerCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFollowerCount(accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.followerCount -> (a.followerCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFollower(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.follower        -> true
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnFollower(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.follower        -> false
        )
    }
    run(q).map(_ == 1)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) = {

    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(r => r.accountId == lift(accountId) && r.followed  == true && r.followedAt < lift(s))
        .sortBy(_.followedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
        .join(query[Accounts]).on((r, a) => a.id == r.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
    }
    run(q).map(_.map({ case ((f, a), r) => (a.copy(position = f.followedAt), r)}))

  }


}
