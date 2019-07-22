package io.github.cactacea.backend.core.util.configs

case class OAuth2Config (
                        authorizationURL: Option[String],
                        accessTokenURL: Option[String],
                        redirectURL: Option[String],
                        clientID: Option[String],
                        clientSecret: Option[String],
                        scope: Option[String]
                      )
