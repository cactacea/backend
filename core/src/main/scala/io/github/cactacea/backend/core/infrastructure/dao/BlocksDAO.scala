package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, BlockId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class BlocksDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    _updateBlocks(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        _insertBlocks(accountId, sessionId).map(_ => Unit)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateUnblocks(accountId, sessionId)
  }

  private def _insertBlocks(accountId: AccountId, sessionId: SessionId): Future[BlockId] = {
    val by = sessionId.toAccountId
    val blockedAt = timeService.nanoTime()
    val q = quote {
      query[Blocks]
        .insert(
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.blocked       -> true,
          _.blockedAt     -> lift(blockedAt)
        ).returning(_.id)
    }
    run(q)
  }

  private def _updateBlocks(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val blockedAt = timeService.nanoTime()
    val q = quote {
      query[Blocks]
        .filter(_.accountId == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.blocked        -> true,
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
          _.blocked        -> false
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
        .filter(_.blocked    == true)
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Blocks)]] = {
    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId
    val status = AccountStatusType.normally

    val q = quote {
      query[Blocks].filter(b => b.by == lift(by) && b.blocked == true && (b.id < lift(s) || lift(s) == -1L))
        .join(query[Accounts]).on((b, a) => a.id == b.accountId && a.accountStatus == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by) })
        .map({ case ((b, a), r) => (a, r, b)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)
  }

}
