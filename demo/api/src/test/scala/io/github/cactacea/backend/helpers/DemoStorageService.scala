package io.github.cactacea.backend.helpers

import java.io.{BufferedOutputStream, FileInputStream}
import java.nio.file.{Files, Paths}
import java.util.UUID

import com.twitter.conversions.StorageUnitOps._
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.finatra.http.request.RequestUtils
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.backend.core.application.components.interfaces.StorageService
import io.github.cactacea.backend.core.domain.models.StorageFile
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.media.MediaExtractor
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{FileSizeLimitExceededError, UploadFileNotFound}
import org.apache.commons.io.IOUtils
import resource._

class DemoStorageService(val localPath: String) extends StorageService {

  override def get(request: Request): Future[Response] = {
    FuturePool.unboundedPool {
      val response = Response(Version.Http11, Status.Ok)
      response.withOutputStream { outputStream =>
        val f = new FileInputStream(localPath + request.params("*"))
        val arr = IOUtils.toByteArray(f)
        outputStream.write(arr)
      }
      response
    }
  }

  override def put(request: Request): Future[Seq[StorageFile]] = {
    val multiParams = RequestUtils.multiParams(request)
    if (multiParams.size == 0) {
      Future.exception(CactaceaException(UploadFileNotFound))
    } else {
      val mediums = multiParams.toList.flatMap({ case (_, item) =>
        MediaExtractor.extract(item.contentType, item.data).map((_, item.filename.getOrElse(UUID.randomUUID.toString))) })
      if (mediums.filter({ case (m, _) => m.data.size.bytes > Config.storage.maxFileSize}).size > 0) {
        Future.exception(CactaceaException(FileSizeLimitExceededError))
      } else {
        Future.traverseSequentially(mediums) { case (medium, filename) =>
          FuturePool.unboundedPool {
            val host = Config.storage.hostName
            val url = s"http://${host}:${9000}/mediums/" + filename
            val filePath = localPath + filename
            for {
              out <- managed(new BufferedOutputStream(new java.io.FileOutputStream(filePath)))
            } {
              out.write(medium.data)
            }
            StorageFile(filename, url, Some(url), medium.width, medium.height, medium.data.length, medium.mediumType)
          }
        }
      }
    }
  }

  override def delete(key: String): Future[Unit] = {
    FuturePool.unboundedPool {
      val path = Paths.get(key)
      if (Files.exists(path)) {
        Files.delete(path)
      }
      ()
    }
  }

}