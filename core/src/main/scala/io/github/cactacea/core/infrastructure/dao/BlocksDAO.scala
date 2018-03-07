package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.enums.AccountStatusType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class BlocksDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateBlocks(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertBlocks(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateUnblocks(accountId, sessionId)
  }

  private def _insertBlocks(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val blockedAt = System.nanoTime()
    val q = quote {
      query[Blocks]
        .insert(
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.blocked      -> lift(true),
          _.blockedAt     -> lift(blockedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateBlocks(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val blockedAt = System.nanoTime()
    val q = quote {
      query[Blocks]
        .filter(_.accountId == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.blocked        -> lift(true),
          _.blockedAt       -> lift(blockedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateUnblocks(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.blocked        -> lift(false)
        )
    }
    run(q).map(_ == 1)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .filter(_.blocked    == lift(true))
        .size
    }
    run(q).map(_ == 1)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships])]] = {
    val s = since.getOrElse(Long.MaxValue)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId
    val status = AccountStatusType.singedUp

    val q = quote {
      query[Blocks].filter(b => b.by == lift(by) && b.blocked == true && b.blockedAt < lift(s))
        .join(query[Accounts]).on((b, a) => a.id == b.accountId && a.accountStatus == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by) })
        .sortBy({ case ((b, _), _) => b.blockedAt})(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }

    run(q)
      .map(_.map({ case ((b, a), r) => (a.copy(position = b.blockedAt), r)}))
  }

}
