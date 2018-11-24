package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class BlocksDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val blockedAt = timeService.currentTimeMillis()
    val q = quote {
      query[Blocks]
        .insert(
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.blockedAt     -> lift(blockedAt)
        ).returning(_.id)
    }
    run(q).map(_ => Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Blocks)]] = {

    val by = sessionId.toAccountId
    val status = AccountStatusType.normally

    val q = quote {
      query[Blocks]
        .filter(b => b.by == lift(by) && lift(since).forall(s => b.id < s))
        .join(query[Accounts]).on((b, a) => a.id == b.accountId && a.accountStatus == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by) })
        .map({ case ((b, a), r) => (a, r, b)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(offset).getOrElse(0))
        .take(lift(count).getOrElse(20))
    }
    run(q)
  }

}
