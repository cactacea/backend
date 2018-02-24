package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class AuthCodes (
                  authorizationCode: String,
                  accountId: AccountId,
                  redirectUri: Option[String],
                  createdAt: Long,
                  scope: Option[String],
                  clientId: String,
                  expiresIn: Long
                )
