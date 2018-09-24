package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MuteId}

case class Mutes(
                   id: MuteId,
                   accountId: AccountId,
                   by: AccountId,
                   mutedAt: Long
                        )
