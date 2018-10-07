package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status

trait CactaceaError {

  val code: Int
  val message: String

  def status: Status

}
