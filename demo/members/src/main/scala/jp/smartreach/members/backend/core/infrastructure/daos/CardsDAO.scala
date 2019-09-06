package jp.smartreach.members.backend.core.infrastructure.daos

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import jp.smartreach.members.backend.core.infrastructure.identifiers.{CardId, MemberId}
import jp.smartreach.members.backend.core.infrastructure.models.Cards

class CardsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(mediumId: MediumId, by: MemberId): Future[CardId] = {
    val createdAt = System.currentTimeMillis()
    val q = quote {
      query[Cards]
        .insert(
          _.mediumId    -> lift(mediumId),
          _.by          -> lift(by),
          _.createdAt   -> lift(createdAt)
      ).returning(_.id)
    }
    run(q)
  }

  def update(id: CardId, mediumId: MediumId): Future[Unit] = {
    val q = quote {
      query[Cards]
        .filter(_.id == lift(id))
        .update(_.mediumId -> lift(mediumId))
    }
    run(q).map(_ => ())
  }

  def exists(id: CardId): Future[Boolean] = {
    val q = quote {
      query[Cards]
        .filter(_.id == lift(id))
        .nonEmpty
    }
    run(q)
  }

  def delete(id: CardId): Future[Unit] = {
    val q = quote {
      query[Cards]
        .filter(_.id == lift(id))
        .delete
    }
    run(q).map(_ => ())
  }

}
