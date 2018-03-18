package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class FollowingDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _
  @Inject private var identifyService: IdentifyService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    (for {
      m <- identifyService.generate()
      _ <- _updateFollowCount(sessionId)
      r <- _updateFollow(accountId, m, sessionId)
    } yield ((m, r))).flatMap(_ match {
      case (_, true) =>
        Future.True
      case (m, false) =>
        _insertFollow(accountId, m, sessionId)
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

  private def _insertFollow(accountId: AccountId, followedAt: Long, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.follow        -> true,
          _.followedAt      -> lift(followedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFollow(accountId: AccountId, followedAt: Long, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.follow        -> true,
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
          _.follow          -> false
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
        .filter(_.follow     == true)
        .size
    }
    run(q).map(_ == 1)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Long)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(f => f.by == lift(by) && f.follow  == true && (f.followedAt < lift(s) || lift(s) == -1L) )
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((f, _), _) => f.followedAt})(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.map({ case ((f, a), r) => (a, r, f.followedAt)}).sortBy(_._3).reverse)

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Long)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(f => f.by == lift(accountId) && f.follow  == true && (f.followedAt < lift(s) || lift(s) == -1L) )
        .filter(r => query[Blocks]
          .filter(_.accountId == r.accountId)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == true || b.beingBlocked == true)
          .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((f, _), _) => f.followedAt})(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.map({ case ((f, a), r) => (a, r, f.followedAt)}).sortBy(_._3).reverse)

  }

}