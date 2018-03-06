package io.github.cactacea.core.application.components.services

import java.nio.file.{Files, Paths}
import java.util.UUID

import com.google.inject.Singleton
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.core.application.components.interfaces.StorageService
import resource._

@Singleton
class DefaultStorageService(val endpoint: String, val path: String) extends StorageService {

  override def put(contentType: Option[String], data: Array[Byte]): Future[(String, String)] = {
    FuturePool.unboundedPool {
      val uuid = UUID.randomUUID.toString
      val url = endpoint + "/" + uuid
      val filePath = path + uuid
      for {
        out <- managed(new java.io.BufferedOutputStream(new java.io.FileOutputStream(filePath)))
      } {
        out.write(data)
      }
      (url, filePath)
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
