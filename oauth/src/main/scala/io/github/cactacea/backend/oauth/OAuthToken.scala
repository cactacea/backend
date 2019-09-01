package io.github.cactacea.backend.oauth

import java.util.Date

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class OAuthToken(
                       userId: UserId,
                       issuedAt: Date,
                       expiresIn: Option[Long],
                       clientId: String,
                       redirectUri: Option[String],
                       scope: Option[String]
                           )
