package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId}

case class FriendRequests(
                           id: FriendRequestId,
                           accountId: AccountId,
                           by: AccountId,
                           notified: Boolean,
                           requestedAt: Long
                         )
