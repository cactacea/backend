package io.github.cactacea.backend.views

import com.twitter.finatra.response.Mustache


@Mustache("signin")
case class SignInView (
                        responseType: String,
                        clientId: String,
                        redirectUri: String,
                        scope: Option[String],
                        state: Option[String],
                        codeChallenge: Option[String],
                        codeChallengeMethod: Option[String]
                      )

