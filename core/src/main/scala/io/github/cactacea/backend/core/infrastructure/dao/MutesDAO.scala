package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class MutesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    (for {
      r <- _updateMuted(accountId, sessionId)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        _insertMuted(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.mute           -> false
        )
    }
    run(q).map(_ == 1)

  }

  private def _insertMuted(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val mutedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.mute            -> true,
          _.mutedAt         -> lift(mutedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  private def _updateMuted(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val mutedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.mute            -> true,
          _.mutedAt         -> lift(mutedAt)
        )
    }
    run(q).map(_ == 1)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by        == lift(by))
        .filter(_.mute     == true)
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Long)]] = {

    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(f => f.by == lift(by) && f.mute  == true && (f.mutedAt < lift(s) || lift(s) == -1L) )
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((f, _), _) => (f.mutedAt, f.by) })(Ord(Ord.desc, Ord.desc))
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.map({ case ((f, a), r) => (a, r, f.mutedAt)})) //.sortWith(_._3 > _._3))

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Relationships)]] = {

    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(f => f.by == lift(accountId) && f.mute  == true && (f.mutedAt < lift(s) || lift(s) == -1L) )
        .filter(r => query[Blocks]
          .filter(_.accountId == r.accountId)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == true || b.beingBlocked == true)
          .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy(_._3.mutedAt)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

}
