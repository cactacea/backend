package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FriendId}

case class Friends(
                    id: FriendId,
                    userId: UserId,
                    by: UserId,
                    friendedAt: Long
                        )
