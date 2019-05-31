package io.github.cactacea.backend.utils.oauth

import java.util.Date

case class OAuthParsedToken(
                            audience: Long,
                            issuedAt: Date,
                            expiration: Long,
                            clientId: String,
                            scope: Option[String],

                           )