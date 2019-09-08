package io.github.cactacea.backend.core.application.components.services

import java.io.{BufferedOutputStream, FileInputStream}
import java.nio.file.{Files, Paths}
import java.util.UUID
import java.util.concurrent.Executors

import com.twitter.concurrent.NamedPoolThreadFactory
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

class DefaultStorageService(val localPath: String) extends StorageService {

  val fixedThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
    new NamedPoolThreadFactory("QueueFuturePool", makeDaemons = true)
  )
  val futurePool: FuturePool = FuturePool(fixedThreadExecutor)

  override def get(request: Request): Future[Response] = {
    futurePool {
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
    val mediums = multiParams.toSeq.flatMap({ case (_, item) => MediaExtractor.extract(item.contentType, item.data) })
    val count = mediums.size
    val overCount = mediums.filter(_.data.size.bytes > Config.storage.maxFileSize).size
    (count, overCount) match {
      case (0, _) =>
        Future.exception(CactaceaException(UploadFileNotFound))
      case (_, 0) =>
        Future.traverseSequentially(mediums) { medium =>
          futurePool {
            val filename = UUID.randomUUID.toString
            val host = Config.storage.hostName
            val url = s"http://${host}${Config.storage.port}/mediums/" + filename
            val filePath = localPath + filename
            for {
              out <- managed(new BufferedOutputStream(new java.io.FileOutputStream(filePath)))
            } {
              out.write(medium.data)
            }
            StorageFile(filename, url, Some(url), medium.width, medium.height, medium.data.length, medium.mediumType)
          }
        }
      case _ =>
        Future.exception(CactaceaException(FileSizeLimitExceededError))
    }
  }

  override def delete(key: String): Future[Unit] = {
    futurePool {
      val path = Paths.get(key)
      if (Files.exists(path)) {
        Files.delete(path)
      }
      ()
    }
  }

}

