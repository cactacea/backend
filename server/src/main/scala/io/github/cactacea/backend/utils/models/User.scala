package io.github.cactacea.backend.utils.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.infrastructure.models.Accounts
import io.github.cactacea.filhouette.api.Identity

case class User(id: AccountId) extends Identity

object User {

  def apply(a: Accounts): User = {
    User(a.id)
  }

}
