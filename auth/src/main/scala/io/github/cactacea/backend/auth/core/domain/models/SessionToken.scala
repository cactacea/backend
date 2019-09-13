package io.github.cactacea.backend.auth.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class SessionToken(tokenString: String, identifier: String, userId: Option[UserId])
