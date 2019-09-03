package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class UserAuthentications (
                             userId: UserId,
                             providerId: String,
                             providerKey: String
                           )
