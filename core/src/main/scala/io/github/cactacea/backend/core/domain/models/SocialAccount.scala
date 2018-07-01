package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.models.SocialAccounts

case class SocialAccount(
                          providerId: String,
                          providerKey: String
                        )

object SocialAccount {

  def apply(a: SocialAccounts): SocialAccount = {
    SocialAccount(
      providerId = a.providerId,
      providerKey = a.providerKey
    )
  }

}