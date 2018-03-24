package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.models.SocialAccounts

case class SocialAccount(
                       accountType: String,
                       token: String
                   )

object SocialAccount {

  def apply(a: SocialAccounts): SocialAccount = {
    SocialAccount(
      accountType = a.socialAccountType,
      token       = a.socialAccountId
    )
  }

}