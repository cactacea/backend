package io.github.cactacea.addons.aws.storage

import java.util.UUID

import com.twitter.conversions.StorageUnitOps._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.request.RequestUtils
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.StorageService
import io.github.cactacea.backend.core.domain.models.StorageFile
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.media.MediaExtractor
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{FileSizeLimitExceededError, UploadFileNotFound}

class AWSS3StorageService extends StorageService {

  def get(request: Request): Future[Response] = {
    Future.exception(new RuntimeException())
  }

  def put(request: Request): Future[Seq[StorageFile]] = {
    val multiParams = RequestUtils.multiParams(request)
    val mediums = multiParams.toList.flatMap({ case (_, item) => MediaExtractor.extract(item.contentType, item.data) })
    if (mediums.size == 0) {
      Future.exception(CactaceaException(UploadFileNotFound))
    } else if (mediums.filter(_.data.size.bytes > Config.storage.maxFileSize).size > 0) {
      Future.exception(CactaceaException(FileSizeLimitExceededError))
    } else {

      Future.traverseSequentially(mediums) { medium =>
        val key = UUID.randomUUID.toString
        val data = medium.data
        val result = AWSS3.upload(medium.contentType, key, data).map({ url =>
          StorageFile(
            key,
            url.toExternalForm,
            Some(url.toExternalForm),
            medium.width,
            medium.height,
            medium.data.length,
            medium.mediumType)
        })
        result
      }
    }
  }

  def delete(key: String): Future[Unit] = {
    AWSS3.delete(key)
  }

}
