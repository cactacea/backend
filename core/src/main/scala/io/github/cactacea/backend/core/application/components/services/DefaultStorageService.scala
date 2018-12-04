package io.github.cactacea.backend.core.application.components.services

import java.io.{BufferedOutputStream, FileInputStream}
import java.net.InetAddress
import java.nio.file.{Files, Paths}
import java.util.UUID

import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.finatra.http.request.RequestUtils
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.backend.core.application.components.interfaces.StorageService
import io.github.cactacea.backend.core.domain.models.StorageFile
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.media.MediaExtractor
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.NotAcceptableMimeTypeFound
import org.apache.commons.io.IOUtils
import resource._

class DefaultStorageService(val localPath: String) extends StorageService {

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

  override def put(request: Request): Future[StorageFile] = {
    val multiParams = RequestUtils.multiParams(request)
    multiParams.toList.flatMap({ case (_, item) => MediaExtractor.extract(item.contentType, item.data) }).headOption match {
      case Some(media) =>
        FuturePool.unboundedPool {
          val filename = UUID.randomUUID.toString
          val host = InetAddress.getLocalHost().getHostAddress()
          val url = s"http://${host}:${9000}/mediums/" + filename
          val filePath = localPath + filename
          for {
            out <- managed(new BufferedOutputStream(new java.io.FileOutputStream(filePath)))
          } {
            out.write(media.data)
          }
          StorageFile(localPath, url, media.width, media.height, media.data.length, media.mediumType)
        }
      case None =>
        Future.exception(CactaceaException(NotAcceptableMimeTypeFound))
    }
  }

  override def delete(key: String): Future[Boolean] = {
    FuturePool.unboundedPool {
      val path = Paths.get(key)
      if (Files.exists(path)) {
        Files.delete(path)
      }
      true
    }
  }

}
