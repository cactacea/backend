package io.github.cactacea.backend.services

import java.util.UUID

import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.core.infrastructure.services.StorageService
import resource._

class DefaultStorageService extends StorageService {

  override def put(contentType: Option[String], data: Array[Byte]): Future[String] = {
    FuturePool.unboundedPool {
      val uuid = UUID.randomUUID.toString
      val filename = uuid
      for {
        out <- managed(new java.io.BufferedOutputStream(new java.io.FileOutputStream(filename)))
      } {
        out.write(data)
      }
      "http://localhost:9000/images/" + filename
    }
  }

  override def delete(uri: String): Future[Boolean] = {
    Future.True
  }

}
