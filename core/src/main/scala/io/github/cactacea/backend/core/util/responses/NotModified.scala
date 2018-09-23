package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait NotModified extends CactaceaError {
  val code: Int
  val message: String
  override def status = {
    Status.NotModified
  }
}
