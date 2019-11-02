package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{InformationType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{InformationId, UserId}

case class Informations(
                         id: InformationId,
                         userId: UserId,
                         by: UserId,
                         informationType: InformationType,
                         contentId: Option[Long],
                         url: String,
                         unread: Boolean,
                         notifiedAt: Long
                         )
