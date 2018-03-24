package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Relationships

@Singleton
class FriendRequestsStatusDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateRelationship(accountId, true, sessionId).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertRelationship(accountId, true, sessionId)
    })
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    _updateRelationship(accountId, false, sessionId)
  }

  private def _insertRelationship(accountId: AccountId, inProgress: Boolean, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId    -> lift(accountId),
          _.by           -> lift(by),
          _.inProgress   -> lift(inProgress)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateRelationship(accountId: AccountId, inProgress: Boolean, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .update(
          _.inProgress      -> lift(inProgress)
        )
    }
    run(q).map(_ == 1)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .filter(_.accountId    == lift(accountId))
        .filter(_.by           == lift(by))
        .filter(_.inProgress   == true)
        .size
    }
    run(q).map(_ == 1)
  }

}
