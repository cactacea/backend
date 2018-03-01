package io.github.cactacea.backend.models.responses

case class AccessCodeCreated (
                               code: String,
                               state: Option[String]
                             )
