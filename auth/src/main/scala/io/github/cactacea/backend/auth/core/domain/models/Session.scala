package io.github.cactacea.backend.auth.core.domain.models

case class Session (identifier: String, token: String)

object Session {
  def apply(identifier: String): Session = {
    Session(identifier, "")
  }
}
