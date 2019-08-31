package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.infrastructure.dao.MediumsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.MediumsValidator


class MediumsRepository @Inject()(
                                 mediumsValidator: MediumsValidator,
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

    mediumsDAO.create(key, url, thumbnailUrl, mediumType, width, height, size, sessionId)
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[String] = {
    for {
      m <- mediumsValidator.mustFind(mediumId, sessionId)
      _ <- mediumsDAO.delete(mediumId)
    } yield (m.key)
  }

}
