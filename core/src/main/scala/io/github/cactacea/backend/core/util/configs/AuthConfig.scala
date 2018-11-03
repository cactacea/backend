package io.github.cactacea.backend.core.util.configs

import com.twitter.util.Duration

case class AuthConfig (
                      apiKeyHeaderName: Option[String],
                      authorizationHeaderName: Option[String],
                      iosApiKey: Option[String],
                      androidApiKey: Option[String],
                      webApiKey: Option[String],
                      signingKey: Option[String],
                      algorithm: Option[String],
                      expire: Option[Duration],
                      issuer: Option[String],
                      subject: Option[String]
                      )
