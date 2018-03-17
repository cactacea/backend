package io.github.cactacea.core.util.responses

import com.twitter.finagle.http.Status

trait CactaceaError {
  def status: Status
}

