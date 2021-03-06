package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MediumType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Mediums

@Singleton
class MediumsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(key: String,
             uri: String,
             thumbnailUrl: Option[String],
             mediumType: MediumType,
             width: Int,
             height: Int,
             size: Long,
             sessionId: SessionId): Future[MediumId] = {

    val by = sessionId.userId
    val q = quote {
      query[Mediums].insert(
        _.key             -> lift(key),
        _.uri             -> lift(uri),
        _.width           -> lift(width),
        _.height          -> lift(height),
        _.size            -> lift(size),
        _.by              -> lift(by),
        _.thumbnailUrl    -> lift(thumbnailUrl),
        _.mediumType      -> lift(mediumType),
        _.contentWarning  -> false,
        _.contentStatus   -> lift(ContentStatusType.unchecked)
      ).returningGenerated(_.id)
    }
    run(q)
  }

  def delete(id: MediumId): Future[Unit] = {
    val q = quote {
      query[Mediums]
        .filter(_.id == lift(id))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(mediumId: MediumId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Mediums]
        .filter(_.id == lift(mediumId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def exists(mediumIds: Seq[MediumId], sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Mediums]
        .filter(m => liftQuery(mediumIds).contains(m.id))
        .filter(_.by == lift(by))
        .size
    }
    run(q).map(_ == mediumIds.size)
  }

  def find(id: MediumId, sessionId: SessionId): Future[Option[Mediums]] = {
    val by = sessionId.userId
    val q = quote {
      query[Mediums]
        .filter(_.id == lift(id))
        .filter(_.by == lift(by))
    }
    run(q).map(_.headOption)
  }


}
