package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId}

case class Blocks(
                   accountId: AccountId,
                   by: AccountId,
                   blocked: Boolean,
                   beingBlocked: Boolean,
                   blockedAt: Long
                        )
