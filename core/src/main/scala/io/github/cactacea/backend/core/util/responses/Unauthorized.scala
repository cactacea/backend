package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait Unauthorized extends CactaceaError {
  override def status: Status = {
    Status.Unauthorized
  }
}
