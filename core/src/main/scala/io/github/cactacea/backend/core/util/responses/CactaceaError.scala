package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait CactaceaError {
  def status: Status
}

