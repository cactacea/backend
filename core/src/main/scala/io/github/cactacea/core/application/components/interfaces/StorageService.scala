package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future

trait StorageService  {

  def put(contentType: Option[String], data: Array[Byte]): Future[String]
  def delete(uri: String): Future[Boolean]

}
