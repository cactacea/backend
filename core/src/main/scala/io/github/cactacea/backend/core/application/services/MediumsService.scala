package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.StorageService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.repositories.MediumsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}

@Singleton
class MediumsService @Inject()(
                                databaseService: DatabaseService,
                                storageService: StorageService,
                                mediumsRepository: MediumsRepository
                              ) {

  import databaseService._

  def find(request: Request): Future[Response] = {
    storageService.get(request)
  }

  def create(request: Request, sessionId: SessionId): Future[Seq[(MediumId, String)]] = {
    transaction {
      for {
        s <- storageService.put(request)
        t <- Future.traverseSequentially(s) { f =>
          mediumsRepository.create(
            f.key,
            f.url,
            f.thumbnailUrl,
            f.mediumType,
            f.width,
            f.height,
            f.length,
            sessionId
          ).map(id => (id, f.url))
        }
      } yield (t)
    }
  }

  def delete(mediumId: MediumId, sessionId: SessionId): Future[Unit] = {
    transaction {
      for {
        key <- mediumsRepository.delete(mediumId, sessionId)
        r <- storageService.delete(key)
      } yield (r)
    }
  }

}

