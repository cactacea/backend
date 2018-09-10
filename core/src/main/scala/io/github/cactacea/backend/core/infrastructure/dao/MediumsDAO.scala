package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.IdentifyService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MediumType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Mediums

@Singleton
class MediumsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def create(key: String, uri: String, thumbnailUri: Option[String], mediumType: MediumType, width: Int, height: Int, size: Long, sessionId: SessionId): Future[MediumId] = {
    for {
      id <- identifyService.generate().map(MediumId(_))
      _ <- insert(id, key, uri, thumbnailUri, mediumType, width, height, size, sessionId)
    } yield (id)
  }

  private def insert(id: MediumId, key: String, uri: String, thumbnailUri: Option[String], mediumType: MediumType, width: Int, height: Int, size: Long, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mediums].insert(
        _.id              -> lift(id),
        _.key             -> lift(key),
        _.uri             -> lift(uri),
        _.width           -> lift(width),
        _.height          -> lift(height),
        _.size            -> lift(size),
        _.by              -> lift(by),
        _.thumbnailUri    -> lift(thumbnailUri),
        _.mediumType      -> lift(mediumType),
        _.contentWarning  -> false,
        _.contentStatus   -> lift(ContentStatusType.unchecked)
      )
    }
    run(q)
  }

  def find(id: MediumId, sessionId: SessionId): Future[Option[Mediums]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mediums]
        .filter(_.id == lift(id))
        .filter(_.by == lift(by))
    }
    run(q).map(_.headOption)
  }

  def delete(id: MediumId): Future[Long] = {
    val q = quote {
      query[Mediums]
        .filter(_.id == lift(id))
        .delete
    }
    run(q)
  }

  def exist(mediumId: MediumId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mediums]
        .filter(_.id == lift(mediumId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def exist(mediumIds: List[MediumId], sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mediums]
        .filter(m => liftQuery(mediumIds).contains(m.id))
        .filter(_.by == lift(by))
        .size
    }
    run(q).map(_ == mediumIds.size)
  }
}
