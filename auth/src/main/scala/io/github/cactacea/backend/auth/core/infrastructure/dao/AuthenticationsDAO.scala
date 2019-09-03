package io.github.cactacea.backend.auth.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.core.infrastructure.models.Authentications
import io.github.cactacea.backend.core.application.components.services.DatabaseService

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

  def updateProviderKey(providerId: String, oldProviderKey: String, newProviderKey: String): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(oldProviderKey))
        .update(
          _.providerKey    -> lift(newProviderKey)
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

  def find(providerId: String, providerKey: String): Future[Option[Authentication]] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
    }
    run(q).map(_.headOption.map(Authentication(_)))
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
