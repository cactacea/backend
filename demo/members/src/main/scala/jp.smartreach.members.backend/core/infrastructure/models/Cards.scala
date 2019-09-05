package jp.smartreach.members.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import jp.smartreach.members.backend.core.infrastructure.identifiers.{CardId, MemberId}

case class Cards (
                   id: CardId,
                   mediumId: MediumId,
                   by: MemberId,
                   createdAt: Long
                 )
