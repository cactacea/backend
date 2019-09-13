package io.github.cactacea.backend.auth.core.domain.models

import io.github.cactacea.backend.auth.core.infrastructure.models.Authentications
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.filhouette.api.Identity

case class Authentication(
                           providerId: String,
                           providerKey: String,
                           password: String,
                           hasher: String,
                           confirm: Boolean,
                           userId: Option[UserId]
                         ) extends Identity

object Authentication {

  def apply(a: Authentications): Authentication = {
    Authentication(a.providerId, a.providerKey, a.password, a.hasher, a.confirm, a.userId)
  }

}
