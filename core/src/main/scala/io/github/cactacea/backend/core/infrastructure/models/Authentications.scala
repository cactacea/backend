package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class Authentications (
                     providerId: String,
                     providerKey: String,
                     password: String,
                     hasher: String,
                     confirm: Boolean,
                     accountId: Option[AccountId]
                     )
