package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.SocialAccounts

@Singleton
class SocialAccountsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(providerId: String, providerKey: String, authenticationCode: Option[String], verified: Boolean, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .insert(
          _.accountId    -> lift(accountId),
          _.providerKey  -> lift(providerKey),
          _.providerId   -> lift(providerId),
          _.verified     -> lift(verified)
        )
    }
    run(q).map(_ == 1)
  }

  def update(providerKey: String, verified: Boolean, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
          .filter(_.providerId == lift(providerKey))
          .filter(_.accountId == lift(accountId))
          .update(
            _.verified            -> lift(verified)
          )
    }
    run(q).map(_ == 1)
  }

  def delete(providerId: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.providerId == lift(providerId))
        .filter(_.accountId == lift(accountId))
        .delete
    }
    run(q).map(_ == 1)
  }

  def find(providerId: String, providerKey: String): Future[Option[SocialAccounts]] = {
    val q = quote {
      query[SocialAccounts]
        .filter(_.providerKey == lift(providerKey))
        .filter(_.providerId == lift(providerId))
    }
    run(q).map(_.headOption)
  }

  def exist(providerId: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.providerId == lift(providerId))
        .nonEmpty
    }
    run(q)
  }

  def findAll(sessionId: SessionId): Future[List[SocialAccounts]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.providerId != "")
        .sortBy(_.providerId)
    }
    run(q)
  }

}
