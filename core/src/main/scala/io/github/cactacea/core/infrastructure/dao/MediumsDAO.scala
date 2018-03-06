package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.core.infrastructure.models.Mediums
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class MediumsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(key: String, uri: String, thumbnailUri: Option[String], mediumType: MediumType, width: Int, height: Int, size: Long, sessionId: SessionId): Future[MediumId] = {
    for {
      id <- identifiesDAO.create().map(MediumId(_))
      _ <- insert(id, key, uri, thumbnailUri, mediumType, width, height, size, sessionId)
    } yield (id)
  }

  private def insert(id: MediumId, key: String, uri: String, thumbnailUri: Option[String], mediumType: MediumType, width: Int, height: Int, size: Long, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Mediums].insert(
        _.id            -> lift(id),
        _.key           -> lift(key),
        _.uri           -> lift(uri),
        _.width         -> lift(width),
        _.height        -> lift(height),
        _.size          -> lift(size),
        _.by            -> lift(by),
        _.thumbnailUri  -> lift(thumbnailUri),
        _.mediumType    -> lift(mediumType.toValue)
      )
    }
    run(q)
  }

  def find(id: MediumId): Future[Option[Mediums]] = {
    val q = quote {
      query[Mediums]
        .filter(_.id == lift(id))
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
        .size
    }
    run(q).map(_ == 1)
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
