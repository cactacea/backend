package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.domain.models.SocialAccount
import io.github.cactacea.core.infrastructure.models.SocialAccounts

object SocialAccountFactory {

  def create(a: SocialAccounts): SocialAccount = {
    SocialAccount(
      accountType = SocialAccountType.forName(a.socialAccountType),
      token       = a.socialAccountId
    )
  }
}
