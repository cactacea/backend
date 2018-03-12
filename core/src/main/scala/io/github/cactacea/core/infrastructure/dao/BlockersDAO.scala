package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, BlockId, SessionId}
import io.github.cactacea.core.infrastructure.models.Blocks

@Singleton
class BlockersDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateBlocks(accountId, sessionId, true).flatMap(_ match {
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
    _updateBlocks(accountId, sessionId, false).map(_ => true)
  }

  private def _insertBlocks(id: BlockId, accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .insert(
          _.id            -> lift(id),
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.beingBlocked  -> true
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
