package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait BadRequest extends CactaceaError {
  override def status = {
    Status.BadRequest
  }
}
