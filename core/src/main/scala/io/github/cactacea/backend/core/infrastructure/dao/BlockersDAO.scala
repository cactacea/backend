package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, BlockId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Blocks

@Singleton
class BlockersDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    _updateBlocks(accountId, sessionId, true).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        for {
          _ <- _insertBlocks(accountId, sessionId)
        } yield (Unit)

    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateBlocks(accountId, sessionId, false).map(_ => true)
  }

  private def _insertBlocks(accountId: AccountId, sessionId: SessionId): Future[BlockId] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .insert(
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.beingBlocked  -> true
        ).returning(_.id)
    }
    run(q)
  }

  private def _updateBlocks(accountId: AccountId, sessionId: SessionId, blocker: Boolean): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId == lift(accountId))
        .filter(_.by        == lift(by))
        .update(
          _.beingBlocked         -> lift(blocker)
        )
    }
    run(q).map(_ == 1)
  }


}
