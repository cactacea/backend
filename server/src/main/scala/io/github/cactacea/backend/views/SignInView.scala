package io.github.cactacea.backend.views

import com.twitter.finatra.http.response.Mustache

@Mustache("signIn")
case class SignInView (
                        responseType: String,
                        clientId: String,
                        scope: String,
                        error: Option[String]
                      )

