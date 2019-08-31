package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.Authentications

@Singleton
class AuthenticationsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(providerId: String, providerKey: String, password: String, hasher: String): Future[Unit] = {
    val q = quote {
      query[Authentications].insert(
        _.providerId  -> lift(providerId),
        _.providerKey -> lift(providerKey),
        _.password    -> lift(password),
        _.hasher      -> lift(hasher),
        _.confirm     -> false
      )
    }
    run(q).map(_ => ())
  }

  def update(providerId: String, providerKey: String, password: String, hasher: String): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .update(
          _.password  -> lift(password),
          _.hasher    -> lift(hasher)
        )
    }
    run(q).map(_ => ())
  }

  def updateConfirm(providerId: String, providerKey: String, confirm: Boolean): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .update(
          _.confirm    -> lift(confirm)
        )
    }
    run(q).map(_ => ())
  }

  def updateProviderKey(providerId: String, providerKey: String, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.accountId == lift(accountId))
        .update(
          _.providerKey    -> lift(providerKey)
        )
    }
    run(q).map(_ => ())
  }

  def updateAccountId(providerId: String, providerKey: String, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .update(
          _.accountId    -> lift(Option(accountId))
        )
    }
    run(q).map(_ => ())
  }

  def exists(providerId: String, providerKey: String): Future[Boolean] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .nonEmpty
    }
    run(q)
  }

  def find(providerId: String, providerKey: String, confirm: Boolean): Future[Option[Authentications]] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .filter(_.confirm == lift(confirm))
    }
    run(q).map(_.headOption)
  }

  def find(providerId: String, providerKey: String): Future[Option[Authentications]] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
    }
    run(q).map(_.headOption)
  }

  def delete(providerId: String, providerKey: String): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .delete
    }
    run(q).map(_ => ())
  }

}
