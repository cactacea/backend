package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{ListenerService, StorageService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.repositories.MediumsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}

class MediumsService @Inject()(
                                db: DatabaseService,
                                listenerService: ListenerService,
                                storageService: StorageService,
                                mediumsRepository: MediumsRepository
                              ) {

  def find(request: Request): Future[Response] = {
    storageService.get(request)
  }

  def create(request: Request, sessionId: SessionId): Future[Seq[(MediumId, String)]] = {
    for {
      s <- storageService.put(request)
      t <- db.transaction(Future.traverseSequentially(s) { f =>
        mediumsRepository.create(f.key, f.url, f.thumbnailUrl, f.mediumType, f.width, f.height, f.length, sessionId).map(id => (id, f.url))
      })
      _ <- Future.traverseSequentially(t) { case (id, url) =>
        listenerService.mediumCreated(id, url, sessionId)
      }
    } yield (t)
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        key <- mediumsRepository.delete(mediumId, sessionId)
        r <- storageService.delete(key)
      } yield (r)
    }
  }

}

