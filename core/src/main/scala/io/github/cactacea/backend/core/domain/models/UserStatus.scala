package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.ActiveStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class UserStatus(
                       id: UserId,
                       status: ActiveStatusType)
