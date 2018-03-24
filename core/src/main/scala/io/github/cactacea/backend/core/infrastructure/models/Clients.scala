package io.github.cactacea.backend.core.infrastructure.models

case class Clients(
                     id: String,
                     secret: Option[String],
                     redirectUri: String,
                     scope: Option[String]
                   )
