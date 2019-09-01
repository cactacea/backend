package io.github.cactacea.backend.auth.domain.models

import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.filhouette.api.Identity

case class Auth(id: UserId) extends Identity

object Auth {

  def apply(a: User): Auth = {
    Auth(a.id)
  }

}
