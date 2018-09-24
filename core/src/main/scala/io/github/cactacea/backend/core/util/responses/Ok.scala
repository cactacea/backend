package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait Ok extends CactaceaError {
  val code: Int
  val message: String
  override def status = {
    Status.Ok
  }
}
