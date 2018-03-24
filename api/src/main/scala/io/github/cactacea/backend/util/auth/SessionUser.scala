package io.github.cactacea.backend.util.auth

import io.github.cactacea.core.infrastructure.identifiers.SessionId

case class SessionUser(sessionId: SessionId, sessionUdid: String, expiresIn: Long)
