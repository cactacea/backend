package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class BlocksDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val blockedAt = System.currentTimeMillis()
    val q = quote {
      query[Blocks]
        .insert(
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.blockedAt     -> lift(blockedAt)
        ).returning(_.id)
    }
    run(q).map(_ => ())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    val by = sessionId.toAccountId
    val q = quote {
      (for {
        b <- query[Blocks]
          .filter(_.by == lift(by))
          .filter(b => (lift(since).forall(b.id < _)))
        a <- query[Accounts]
          .join(_.id == b.accountId)
          .filter(a => lift(accountName.map(_ + "%")).forall(a.accountName like _))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r, b))
        .sortBy({ case (_, _, b) => b.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, b) => Account(a, r, b)}))
  }

}
