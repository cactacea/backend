package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.dao.{MediumsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{MediumId, SessionId}

@Singleton
class MediumsRepository {

  @Inject private var mediumsDAO: MediumsDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(key: String, url: String, thumbnailUri: Option[String], mediumType: MediumType, width: Int, height: Int, size: Long, sessionId: SessionId): Future[(MediumId, String)] = {
    for {
      id <- mediumsDAO.create(key, url, thumbnailUri, mediumType, width, height, size, sessionId).map((_, url))
    } yield (id)
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[String] = {
    for {
      m <- validationDAO.findMedium(mediumId, sessionId)
      _ <- mediumsDAO.delete(mediumId)
    } yield (m.key)
  }

}
