package io.github.cactacea.core.util.responses

import com.twitter.finagle.http.Status

case class BadRequest(code: Long, message: String) extends CactaceaError {
  override def status = {
    Status.BadRequest
  }
}
