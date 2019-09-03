package io.github.cactacea.backend.auth.core.infrastructure.models

case class Authentications (
                     providerId: String,
                     providerKey: String,
                     password: String,
                     hasher: String,
                     confirm: Boolean
                     )
