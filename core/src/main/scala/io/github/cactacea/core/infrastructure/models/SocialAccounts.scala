package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class SocialAccounts(
                           socialAccountType: Long,
                           socialAccountId: String,
                           accountId: AccountId
                    )
