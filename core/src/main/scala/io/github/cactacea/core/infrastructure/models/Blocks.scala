package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, BlockId}

case class Blocks(
                   id: BlockId,
                   accountId: AccountId,
                   by: AccountId,
                   blocked: Boolean,
                   beingBlocked: Boolean,
                   blockedAt: Long
                        )
