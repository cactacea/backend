package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers._

case class Destination(
                             accountId: AccountId,
                             accountToken: String,
                             accountName: String,
                             by: AccountId
                           )
