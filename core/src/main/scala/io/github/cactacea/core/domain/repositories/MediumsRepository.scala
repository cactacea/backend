package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.dao.MediumsDAO
import io.github.cactacea.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.{MediumNotFound, OperationNotAllowed}

@Singleton
class MediumsRepository {

  @Inject private var mediumsDAO: MediumsDAO = _

  def create(key: String, url: String, thumbnailUri: Option[String], mediumType: MediumType, width: Int, height: Int, size: Long, sessionId: SessionId): Future[(MediumId, String)] = {
    for {
      id <- mediumsDAO.create(key, url, thumbnailUri, mediumType, width, height, size, sessionId).map((_, url))
    } yield (id)
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[String] = {
    mediumsDAO.find(mediumId).flatMap(_ match {
      case Some(m) =>
        if (m.by == sessionId.toAccountId) {
          mediumsDAO.delete(mediumId).map(_ => m.key)
        } else {
          Future.exception(CactaceaException(OperationNotAllowed))
        }
      case None =>
        Future.exception(CactaceaException(MediumNotFound))
    })
  }

}
