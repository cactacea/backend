package io.github.cactacea.backend.utils.auth

import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

case class SessionUser(sessionId: SessionId, sessionUdid: String, expiresIn: Long)
