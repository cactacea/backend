package io.github.cactacea.finagger

import com.twitter.finatra.http.response.Mustache

@Mustache("redoc")
case class Redoc(modelPath: String)
