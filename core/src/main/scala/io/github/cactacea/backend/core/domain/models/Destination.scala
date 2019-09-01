package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers._

case class Destination(
                        userId: UserId,
                        userToken: String,
                        userName: String,
                        by: UserId
                           )
