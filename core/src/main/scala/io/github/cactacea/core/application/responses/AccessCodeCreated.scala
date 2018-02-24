package io.github.cactacea.core.application.responses

case class AccessCodeCreated (
                               code: String,
                               state: Option[String]
                             )
