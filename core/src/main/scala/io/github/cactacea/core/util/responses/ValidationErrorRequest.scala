package io.github.cactacea.core.util.responses

import com.twitter.finagle.http.Status

case class ValidationErrorRequest(code: Int, message: String) extends CactaceaError {
  override def status = {
    Status.BadRequest
  }
}
