package io.github.cactacea.core.application.services


import com.google.inject.Inject
import com.twitter.finatra.http.fileupload.MultipartItem
import com.twitter.util.Future
import io.github.cactacea.core.application.interfaces.StorageService
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.domain.repositories.MediumsRepository
import io.github.cactacea.core.infrastructure.identifiers.{MediumId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService
import io.github.cactacea.core.util.MediaMetadataExtractor
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.NotAcceptableMimeTypeFound

class MediumsService @Inject()(db: DatabaseService) {

  @Inject var storageService: StorageService = _
  @Inject var mediumsRepository: MediumsRepository = _

  def create(multiParams: Map[String, MultipartItem], sessionId: SessionId): Future[Seq[(MediumId, String)]] = {
    val list = multiParams.toList.map({ case (_, item) => MediaMetadataExtractor.extract(item.contentType, item.data) })
    val result = Future.traverseSequentially(list) {
      _ match {
        case Some(metadata) =>
          for {
            uri <- storageService.put(metadata.contentType, metadata.data)
            r <- db.transaction(mediumsRepository.create(uri, uri, None, MediumType.image, metadata.width, metadata.height, metadata.data.length, sessionId))
          } yield (r)

        case None =>
          Future.exception(CactaceaException(NotAcceptableMimeTypeFound))
      }
    }
    result

    //    val v = multiParams.map({ case (_, item) =>
    //      for {
    //        metadata <- MediumMetadataExtractor.extract(item)
    //        uri <- storageService.put(metadata.contentType, metadata.data)
    //        r <-  db.transaction(mediumsRepository.create(uri, uri, None, MediumType.image, metadata.width, metadata.height, metadata.data.length, sessionId))
    //      } yield (r)
    //    }).toSeq
    //    val b = Future.traverseSequentially(v)


    //    multiParams.headOption match {
    //      case Some((a, b)) => {
    //        storageService.put(Some("image/jpeg"), b.data).flatMap({ uri =>
    //          val key = uri
    //          val in = new ByteArrayInputStream(b.data)
    //          val image = ImageIO.read(in)
    //          val width = image.getWidth
    //          val height = image.getHeight
    //          db.transaction {
    //            mediumsRepository.create(key, uri, None, MediumType.image, width, height, b.data.length, sessionId)
    //          }
    //        })
    //      }
    //      case None =>
    //        Future.exception(ValidationException(FileUploadErrorOccurred))
    //    }
    //  }
  }

}