package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.FriendRequestId

case class FriendRequest (
                           id: FriendRequestId,
                           account: Account,
                           requestStatus: Long,
                           requestedAt: Long,
                           next: Long
                         )
