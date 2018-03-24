package io.github.cactacea.backend.core.application.services


import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{InjectionService, StorageService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.domain.repositories.MediumsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.NotAcceptableMimeTypeFound
import io.github.cactacea.util.media.MediaMetadata

class MediumsService {

  @Inject private var db: DatabaseService = _
  @Inject private var injectionService: InjectionService = _
  @Inject private var storageService: StorageService = _
  @Inject private var mediumsRepository: MediumsRepository = _

  def create(media: List[Option[MediaMetadata]], sessionId: SessionId): Future[Seq[(MediumId, String)]] = {
    Future.traverseSequentially(media) {
      _ match {
        case Some(metadata) =>
          for {
            (url, key) <- storageService.put(metadata.contentType, metadata.data)
            r <- db.transaction(mediumsRepository.create(key, url, None, MediumType.image, metadata.width, metadata.height, metadata.data.length, sessionId))
            _ <- injectionService.mediumCreated(r._1, sessionId)
          } yield (r)
        case None =>
          Future.exception(CactaceaException(NotAcceptableMimeTypeFound))
      }
    }
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

