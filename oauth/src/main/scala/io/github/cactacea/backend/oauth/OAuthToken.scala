package io.github.cactacea.backend.oauth

import java.util.Date

case class OAuthToken(
                       userName: String,
                       issuedAt: Date,
                       expiresIn: Option[Long],
                       clientId: String,
                       redirectUri: Option[String],
                       scope: Option[String]
                           )
