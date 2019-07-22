package io.github.cactacea.backend.auth.domain.models

import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.filhouette.api.Identity

case class User(id: AccountId) extends Identity

object User {

  def apply(a: Account): User = {
    User(a.id)
  }

}
