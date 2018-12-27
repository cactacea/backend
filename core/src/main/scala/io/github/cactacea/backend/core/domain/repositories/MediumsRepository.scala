package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.infrastructure.dao.MediumsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}

@Singleton
class MediumsRepository @Inject()(
                                   mediumsDAO: MediumsDAO
                                 ) {

  def create(key: String,
             url: String,
             thumbnailUrl: Option[String],
             mediumType: MediumType,
             width: Int,
             height: Int,
             size: Long,
             sessionId: SessionId): Future[MediumId] = {

    for {
      id <- mediumsDAO.create(key, url, thumbnailUrl, mediumType, width, height, size, sessionId)
    } yield (id)
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[String] = {
    for {
      m <- mediumsDAO.validateFind(mediumId, sessionId)
      _ <- mediumsDAO.delete(mediumId)
    } yield (m.key)
  }

}
