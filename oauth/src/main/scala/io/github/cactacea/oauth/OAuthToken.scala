package io.github.cactacea.oauth

import java.util.Date

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class OAuthToken(
                             accountId: AccountId,
                             issuedAt: Date,
                             expiresIn: Option[Long],
                             clientId: String,
                             redirectUri: Option[String],
                             scope: Option[String]
                           )
