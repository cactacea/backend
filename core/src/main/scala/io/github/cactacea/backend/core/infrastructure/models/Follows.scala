package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FollowId}

case class Follows(
                    id: FollowId,
                    userId: UserId,
                    by: UserId,
                    followedAt: Long
                        )
