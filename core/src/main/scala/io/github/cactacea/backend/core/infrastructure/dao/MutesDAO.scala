package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Mutes, Relationships}

@Singleton
class MutesDAO @Inject()(db: DatabaseService, relationshipsDAO: RelationshipsDAO) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- createMutes(accountId, sessionId)
      _ <- relationshipsDAO.createMute(accountId, sessionId)
    } yield (r)
  }

  private def createMutes(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val mutedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Mutes]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.mutedAt         -> lift(mutedAt)
        )
    }
    run(q).map(_ => ())
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- deleteMutes(accountId, sessionId)
      _ <- relationshipsDAO.deleteMute(accountId, sessionId)
    } yield (r)
  }

  private def deleteMutes(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mutes]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mutes]
        .filter(_.accountId == lift(accountId))
        .filter(_.by        == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    val by = sessionId.toAccountId
    val q = quote {
      (for {
        m <- query[Mutes]
          .filter(m => m.by == lift(by))
          .filter(m => lift(since).forall(m.id < _))
        a <- query[Accounts]
            .join(_.id == m.accountId)
            .filter(a => lift(accountName.map(_ + "%")).forall(a.accountName like _))
        r <- query[Relationships]
            .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r, m.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))
  }


}
