package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait NotFound extends CactaceaError {
  val code: Int
  val message: String
  override def status = {
    Status.NotFound
  }
}
