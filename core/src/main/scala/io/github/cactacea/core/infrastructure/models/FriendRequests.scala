package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{FriendRequestId, AccountId}

case class FriendRequests(
                           id: FriendRequestId,
                           accountId: AccountId,
                           by: AccountId,
                           requestStatus: Long,
                           requestedAt: Long
                         )
