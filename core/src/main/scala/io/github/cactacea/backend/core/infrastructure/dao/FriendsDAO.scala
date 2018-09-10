package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.IdentifyService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class FriendsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _
  @Inject private var identifyService: IdentifyService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    (for {
      f <- identifyService.generate()
      _ <- _updateFriendCount(sessionId)
      r <- _updateFriend(accountId, f, sessionId)
    } yield ((f, r))).flatMap(_ match {
      case (_ , true) =>
        Future.True
      case (f, false)=>
        _insertFriend(accountId, f, sessionId)
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

  private def _insertFriend(accountId: AccountId, friendedAt: Long, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.friend            -> true,
          _.friendedAt        -> lift(friendedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFriend(accountId: AccountId, friendedAt: Long, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .update(
          _.friend          -> true,
          _.friendedAt      -> lift(friendedAt)
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
          _.friend          -> false
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
        .filter(_.friend      == true)
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Relationships)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(r => r.by == lift(by) && r.friend  == true && (r.friendedAt < lift(s) || lift(s) == -1L) )
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.friendedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Relationships)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(r => r.by == lift(accountId) && r.friend  == true && (r.friendedAt < lift(s) || lift(s) == -1L) )
        .filter(r => query[Blocks]
          .filter(_.accountId == r.accountId)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == true || b.beingBlocked == true)
          .isEmpty)
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.friendedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

}
