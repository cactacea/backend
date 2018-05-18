package io.github.cactacea.backend.core.application.components.interfaces

trait HashService {

  def hash(plain: String): String

}
