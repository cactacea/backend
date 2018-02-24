package io.github.cactacea.core.util.oauth

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class OAuthUser(
                        accountId: AccountId,
                        expiresIn: Long
                        )
