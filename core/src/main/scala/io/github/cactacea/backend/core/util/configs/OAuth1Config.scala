package io.github.cactacea.backend.core.util.configs

case class OAuth1Config (
                          requestTokenURL: Option[String],
                          accessTokenURL: Option[String],
                          authorizationURL: Option[String],
                          callbackURL: Option[String],
                          consumerKey: Option[String],
                          consumerSecret: Option[String]
                      )
