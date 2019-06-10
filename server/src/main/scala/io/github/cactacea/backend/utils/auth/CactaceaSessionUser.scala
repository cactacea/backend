package io.github.cactacea.backend.utils.auth

import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

case class CactaceaSessionUser(sessionId: SessionId, sessionUdid: String, expiresIn: Long)
