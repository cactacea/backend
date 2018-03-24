package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.AdvertisementSettings

@Singleton
class AdvertisementSettingsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(ad1: Boolean, ad2: Boolean, ad3: Boolean, ad4: Boolean, ad5: Boolean, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[AdvertisementSettings].insert(
        _.accountId    -> lift(accountId),
        _.ad1       -> lift(ad1),
        _.ad2       -> lift(ad2),
        _.ad3       -> lift(ad3),
        _.ad4       -> lift(ad4),
        _.ad5       -> lift(ad5)
      )
    }
    run(q).map(_ == 1)
  }

  def update(ad1: Boolean, ad2: Boolean, ad3: Boolean, ad4: Boolean, ad5: Boolean, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[AdvertisementSettings]
        .filter(_.accountId == lift(accountId))
        .update(
        _.ad1       -> lift(ad1),
        _.ad2       -> lift(ad2),
        _.ad3       -> lift(ad3),
        _.ad4       -> lift(ad4),
        _.ad5       -> lift(ad5)
      )
    }
    run(q).map(_ == 1)
  }

  def find(sessionId: SessionId): Future[Option[AdvertisementSettings]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[AdvertisementSettings]
        .filter(_.accountId == lift(accountId))
    }
    run(q).map(_.headOption)
  }

}
