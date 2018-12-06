package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{InjectionService, StorageService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.repositories.MediumsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}

class MediumsService @Inject()(
                                db: DatabaseService,
                                injectionService: InjectionService,
                                storageService: StorageService,
                                mediumsRepository: MediumsRepository
                              ) {

  def find(request: Request): Future[Response] = {
    storageService.get(request)
  }

  def create(request: Request, sessionId: SessionId): Future[Seq[(MediumId, String)]] = {
    for {
      s <- storageService.put(request)
      r <- Future.traverseSequentially(s) { f =>
        for {
          id <- mediumsRepository.create(f.key, f.url, None, f.mediumType, f.width, f.height, f.length, sessionId)
          _ <- injectionService.mediumCreated(id, f.url, sessionId)
        } yield ((id, f.url))
      }
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

