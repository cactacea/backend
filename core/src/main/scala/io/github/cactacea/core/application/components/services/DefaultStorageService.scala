package io.github.cactacea.core.application.components.services

import java.util.UUID

import com.google.inject.Singleton
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.core.application.components.interfaces.StorageService
import resource._

@Singleton
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
