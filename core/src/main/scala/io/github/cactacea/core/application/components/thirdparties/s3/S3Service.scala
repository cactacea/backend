package io.github.cactacea.core.application.components.thirdparties.s3

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.StorageService
import io.github.cactacea.core.infrastructure.clients.s3.S3HttpClient
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.FileUploadErrorOccurred

class S3Service extends StorageService {

  @Inject var s3HttpClient: S3HttpClient = _

  override def put(contentType: Option[String], data: Array[Byte]): Future[String] = {
    s3HttpClient.put(contentType, data).map(s => s._2).rescue {
      case _: Exception => Future.exception(CactaceaException(FileUploadErrorOccurred))
    }
  }

  override def delete(uri: String): Future[Boolean] = {
    Future.True
  }

}
