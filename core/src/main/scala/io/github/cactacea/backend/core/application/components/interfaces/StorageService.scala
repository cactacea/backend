package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.StorageFile

trait StorageService  {

  def get(request: Request): Future[Response]
  def put(request: Request): Future[StorageFile]
  def delete(key: String): Future[Boolean]

}
