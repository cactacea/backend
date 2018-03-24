package io.github.cactacea.backend.views

import com.twitter.finatra.response.Mustache

@Mustache("error")
case class ErrorView (applicationName: String, message: String)
