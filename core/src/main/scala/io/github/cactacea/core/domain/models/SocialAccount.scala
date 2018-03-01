package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.infrastructure.models.SocialAccounts

case class SocialAccount(
                       accountType: SocialAccountType,
                       token: String
                   )

object SocialAccount {

  def apply(a: SocialAccounts): SocialAccount = {
    SocialAccount(
      accountType = SocialAccountType.forName(a.socialAccountType),
      token       = a.socialAccountId
    )
  }

}