package io.github.cactacea.backend.utils.auth

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.infrastructure.models.Accounts
import io.github.cactacea.filhouette.api.Identity

case class CactaceaAccount(id: AccountId) extends Identity

object CactaceaAccount {

  def apply(a: Accounts): CactaceaAccount = {
    CactaceaAccount(a.id)
  }

}
