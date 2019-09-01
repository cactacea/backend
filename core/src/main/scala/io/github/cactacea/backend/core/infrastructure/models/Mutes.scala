package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, MuteId}

case class Mutes(
                  id: MuteId,
                  userId: UserId,
                  by: UserId,
                  mutedAt: Long
                        )
