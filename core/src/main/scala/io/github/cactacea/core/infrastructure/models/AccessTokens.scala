package io.github.cactacea.core.infrastructure.models

import java.util.Date

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class AccessTokens (
                          accessToken: String,
                          refreshToken: Option[String],
                          accountId: AccountId,
                          clientId: String,
                          scope: Option[String],
                          expiresIn: Long,
                          createdAt: Date
                        )
