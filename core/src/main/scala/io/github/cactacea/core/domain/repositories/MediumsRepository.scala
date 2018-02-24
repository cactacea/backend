package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.responses.MediumCreated
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.dao.MediumsDAO
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class MediumsRepository {

  @Inject var mediumsDAO: MediumsDAO = _

  def create(key: String, uri: String, thumbnailUri: Option[String], mediumType: MediumType, width: Int, height: Int, size: Long, sessionId: SessionId): Future[MediumCreated] = {
    mediumsDAO.create(key, uri, thumbnailUri, mediumType, width, height, size, sessionId).map(MediumCreated(_, uri))
  }

}
