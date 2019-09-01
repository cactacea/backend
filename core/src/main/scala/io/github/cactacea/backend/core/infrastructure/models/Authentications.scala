package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class Authentications (
                     providerId: String,
                     providerKey: String,
                     password: String,
                     hasher: String,
                     confirm: Boolean,
                     userId: Option[UserId]
                     )
