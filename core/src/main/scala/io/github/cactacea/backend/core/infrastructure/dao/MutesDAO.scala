package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Mutes, Relationships}

@Singleton
class MutesDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val mutedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Mutes]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.mutedAt         -> lift(mutedAt)
        )
    }
    run(q).map(_ => Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mutes]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mutes]
        .filter(_.accountId == lift(accountId))
        .filter(_.by        == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Mutes)]] = {

    val s = since.getOrElse(-1L)


    val by = sessionId.toAccountId

    val q = quote {
      query[Mutes].filter(m => m.by == lift(by) && (m.mutedAt < lift(s) || lift(s) == -1L))
        .join(query[Accounts]).on((m, a) => a.id == m.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((m, a), r) => (a, r, m)})
        .sortBy({ case (_, _, m) => m.mutedAt })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q)

  }

}
