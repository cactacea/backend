package io.github.cactacea.core.application.services


import com.google.inject.Inject
import com.twitter.finatra.http.fileupload.MultipartItem
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, StorageService}
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.domain.repositories.MediumsRepository
import io.github.cactacea.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService
import io.github.cactacea.core.util.MediaMetadataExtractor
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.NotAcceptableMimeTypeFound

class MediumsService @Inject()(db: DatabaseService, storageService: StorageService, mediumsRepository: MediumsRepository, injectionService: InjectionService) {

  def create(multiParams: Map[String, MultipartItem], sessionId: SessionId): Future[Seq[(MediumId, String)]] = {
    val list = multiParams.toList.map({ case (_, item) => MediaMetadataExtractor.extract(item.contentType, item.data) })
    Future.traverseSequentially(list) {
      _ match {
        case Some(metadata) =>
          for {
            uri <- storageService.put(metadata.contentType, metadata.data)
            r <- db.transaction(mediumsRepository.create(uri, uri, None, MediumType.image, metadata.width, metadata.height, metadata.data.length, sessionId))
            _ <- injectionService.mediumCreated(r._1, sessionId)
          } yield (r)
        case None =>
          Future.exception(CactaceaException(NotAcceptableMimeTypeFound))
      }
    }
  }

}

