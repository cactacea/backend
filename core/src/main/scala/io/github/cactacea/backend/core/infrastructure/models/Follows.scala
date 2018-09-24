package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FollowId}

case class Follows(
                    id: FollowId,
                    accountId: AccountId,
                    by: AccountId,
                    followedAt: Long
                        )
