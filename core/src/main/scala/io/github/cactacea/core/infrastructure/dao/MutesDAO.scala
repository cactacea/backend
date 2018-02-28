package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Relationships}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class MutesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateMuted(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertMuted(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateUnMuted(accountId, sessionId).map(_ => true)
  }

  private def _insertMuted(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val mutedAt = System.nanoTime()
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId          -> lift(accountId),
          _.by              -> lift(by),
          _.muted           -> lift(true),
          _.mutedAt         -> lift(mutedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateMuted(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val mutedAt = System.nanoTime()
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.muted           -> lift(true),
          _.mutedAt         -> lift(mutedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnMuted(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.muted           -> lift(false)
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
        .filter(_.muted     == lift(true))
        .size
    }
    run(q).map(_ == 1)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) = {

    val s = since.getOrElse(Long.MaxValue)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId

    val q = quote {
      query[Relationships].filter(r => r.by == lift(accountId) && r.muted  == true && r.mutedAt < lift(s))
        .sortBy(_.mutedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
        .join(query[Accounts]).on((r, a) => a.id == r.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
    }
    run(q).map(_.map({ case ((f, a), r) => (a.copy(position = f.mutedAt), r)}))

  }

}
