package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.MediumsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Mediums
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.MediumNotFound

@Singleton
class MediumsValidator @Inject()(mediumsDAO: MediumsDAO) {

  def find(mediumId: MediumId, sessionId: SessionId): Future[Mediums] = {
    mediumsDAO.find(mediumId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case None =>
        Future.exception(CactaceaException(MediumNotFound))
    })
  }

  def exist(mediumIdsOpt: Option[List[MediumId]], sessionId: SessionId): Future[Unit] = {
    mediumIdsOpt match {
      case Some(mediumIds) =>
        mediumsDAO.exist(mediumIds, sessionId).flatMap(_ match {
          case true =>
            Future.Unit
          case false =>
            Future.exception(CactaceaException(MediumNotFound))
        })
      case None =>
        Future.Unit
    }
  }

  def exist(mediumId: MediumId, sessionId: SessionId): Future[Unit] = {
    mediumsDAO.exist(mediumId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(MediumNotFound))
    })
  }

}
