package io.github.cactacea.backend.externals.infrastructure.storages.s3

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.StorageService
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.FileUploadErrorOccurred

class S3Service @Inject()(s3HttpClient: S3HttpClient) extends StorageService {

  override def put(contentType: Option[String], data: Array[Byte]): Future[(String, String)] = {
    s3HttpClient.put(contentType, data).map({s => (s._1.key, s._2)})
    .rescue {
      case _: Exception => Future.exception(CactaceaException(FileUploadErrorOccurred))
    }
  }

  override def delete(key: String): Future[Boolean] = {
    s3HttpClient.delete(key)
  }

}
