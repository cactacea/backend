package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.infrastructure.dao.{MediumsDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}

@Singleton
class MediumsRepository @Inject()(
                                   mediumsDAO: MediumsDAO,
                                   validationDAO: ValidationDAO
                                 ) {

  def create(key: String,
             url: String,
             thumbnailUri: Option[String],
             mediumType: MediumType,
             width: Int,
             height: Int,
             size: Long,
             sessionId: SessionId): Future[MediumId] = {

    for {
      id <- mediumsDAO.create(key, url, thumbnailUri, mediumType, width, height, size, sessionId)
    } yield (id)
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[String] = {
    for {
      m <- validationDAO.findMedium(mediumId, sessionId)
      _ <- mediumsDAO.delete(mediumId)
    } yield (m.key)
  }

}
