package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait BadRequest extends CactaceaError {
  val code: Int
  val message: String
  override def status = {
    Status.BadRequest
  }
}
