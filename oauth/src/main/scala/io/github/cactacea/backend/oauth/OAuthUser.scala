package io.github.cactacea.backend.oauth

import java.util.Date

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class OAuthUser(userId: UserId, issuedAt: Date)
