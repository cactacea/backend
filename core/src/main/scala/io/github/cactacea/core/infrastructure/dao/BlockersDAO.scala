package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.core.infrastructure.models.Blocks

@Singleton
class BlockersDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateBlocks(accountId, sessionId, true).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertBlocks(accountId, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateBlocks(accountId, sessionId, false).map(_ => true)
  }

  private def _insertBlocks(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .insert(
          _.accountId -> lift(accountId),
          _.by        -> lift(by),
          _.beingBlocked   -> lift(true)
        )
    }
    run(q).map(_ == 1)
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
