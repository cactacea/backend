package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

abstract class CactaceaError {
  val code: Int
  val message: String
  def status: Status
}

