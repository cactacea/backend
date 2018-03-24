package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class SocialAccounts(
                           socialAccountType: String,
                           socialAccountId: String,
                           accountId: AccountId,
                           authenticationCode: Option[String],
                           verified: Boolean
                    )
