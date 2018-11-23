package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{InjectionService, StorageService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.domain.repositories.MediumsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}

class MediumsService @Inject()(
                                db: DatabaseService,
                                injectionService: InjectionService,
                                storageService: StorageService,
                                mediumsRepository: MediumsRepository
                              ) {

  def create(width: Int, height: Int, data: Array[Byte], contentType: Option[String], sessionId: SessionId): Future[(MediumId, String)] = {
    for {
      (url, key) <- storageService.put(contentType, data)
      r <- db.transaction(mediumsRepository.create(key, url, None, MediumType.image, width, height, data.length, sessionId))
      _ <- injectionService.mediumCreated(r._1, r._2, sessionId)
    } yield (r)
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[Boolean] = {
    db.transaction {
      for {
        key <- mediumsRepository.delete(mediumId, sessionId)
        r <- storageService.delete(key)
      } yield (r)
    }
  }

}

