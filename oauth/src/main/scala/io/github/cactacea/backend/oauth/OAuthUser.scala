package io.github.cactacea.backend.oauth

import java.util.Date

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class OAuthUser(accountId: AccountId, issuedAt: Date)
