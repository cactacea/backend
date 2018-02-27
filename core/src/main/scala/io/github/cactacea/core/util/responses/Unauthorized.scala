package io.github.cactacea.core.util.responses

import com.twitter.finagle.http.Status

case class Unauthorized(code: Long, message: String) extends CactaceaError {
  override def status = {
    Status.Unauthorized
  }
}
