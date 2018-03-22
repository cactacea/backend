package io.github.cactacea.core.infrastructure.models

case class Clients(
                     id: String,
                     secret: Option[String],
                     redirectUri: String,
                     scope: Option[String]
                   )
