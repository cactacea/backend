package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class SocialAccounts(
                           socialAccountType: String,
                           socialAccountId: String,
                           accountId: AccountId,
                           authenticationCode: Option[String],
                           verified: Boolean
                    )
