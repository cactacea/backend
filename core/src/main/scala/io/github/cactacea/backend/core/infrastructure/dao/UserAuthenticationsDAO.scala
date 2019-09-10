package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.infrastructure.models.UserAuthentications

@Singleton
class UserAuthenticationsDAO  @Inject()(db: DatabaseService) {

  import db._

  def create(userId: UserId, providerId: String, providerKey: String): Future[Unit] = {
    val q = quote {
      query[UserAuthentications].insert(
        _.providerId  -> lift(providerId),
        _.providerKey -> lift(providerKey),
        _.userId      -> lift(userId)
      )
    }
    run(q).map(_ => ())
  }

  def update(providerId: String, oldProviderKey: String, newProviderKey: String): Future[Unit] = {
    val q = quote {
      query[UserAuthentications]
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
      query[UserAuthentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .nonEmpty
    }
    run(q)
  }

  def exists(userId: UserId, providerId: String, providerKey: String): Future[Boolean] = {
    val q = quote {
      query[UserAuthentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .nonEmpty
    }
    run(q)
  }

}
