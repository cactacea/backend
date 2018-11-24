package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait NotFound extends CactaceaError {
  override def status: Status = {
    Status.NotFound
  }
}
