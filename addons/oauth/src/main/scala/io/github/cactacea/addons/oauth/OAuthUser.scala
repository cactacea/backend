package io.github.cactacea.addons.oauth

import java.util.Date

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class OAuthUser(
                        accountId: AccountId,
                        permissions: Int,
                        createdAt: Date,
                        expiresIn: Long
                        )
