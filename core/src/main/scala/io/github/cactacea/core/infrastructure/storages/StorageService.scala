package io.github.cactacea.core.infrastructure.storages

import com.twitter.util.Future

trait StorageService  {

  def put(contentType: Option[String], data: Array[Byte]): Future[String]
  def delete(uri: String): Future[Boolean]

}
