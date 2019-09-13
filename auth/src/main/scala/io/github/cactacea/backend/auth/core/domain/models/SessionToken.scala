package io.github.cactacea.backend.auth.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class SessionToken(identifier: String, tokenString: String, userId: Option[UserId])
