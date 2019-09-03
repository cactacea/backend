package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, BlockId}

case class Blocks(
                   id: BlockId,
                   userId: UserId,
                   by: UserId,
                   blockedAt: Long
                        )
