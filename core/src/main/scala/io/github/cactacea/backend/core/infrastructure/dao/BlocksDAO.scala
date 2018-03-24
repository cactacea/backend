package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.IdentifyService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, BlockId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}

@Singleton
class BlocksDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _
  @Inject private var identifyService: IdentifyService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateBlocks(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        for {
          id <- identifyService.generate().map(BlockId(_))
          r <- _insertBlocks(id, accountId, sessionId)
        } yield (r)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateUnblocks(accountId, sessionId)
  }

  private def _insertBlocks(id: BlockId, accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val blockedAt = timeService.nanoTime()
    val q = quote {
      query[Blocks]
        .insert(
          _.id            -> lift(id),
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.blocked       -> true,
          _.blockedAt     -> lift(blockedAt)
        )
    }
    run(q).map(_ == 1)
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
        .size
    }
    run(q).map(_ == 1)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Blocks)]] = {
    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId
    val status = AccountStatusType.normally

    val q = quote {
      query[Blocks].filter(b => b.by == lift(by) && b.blocked == true && (infix"b.id < ${lift(s)}".as[Boolean] || lift(s) == -1L))
        .join(query[Accounts]).on((b, a) => a.id == b.accountId && a.accountStatus == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by) })
        .map({ case ((b, a), r) => (a, r, b)})
        .sortBy(_._3.id)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)
  }

}
