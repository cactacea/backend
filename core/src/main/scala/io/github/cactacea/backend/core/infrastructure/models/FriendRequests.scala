package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FriendRequestId}

case class FriendRequests(
                           id: FriendRequestId,
                           userId: UserId,
                           by: UserId,
                           notified: Boolean,
                           requestedAt: Long
                         )
