package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FriendRequestId}

case class FriendRequests(
                           id: FriendRequestId,
                           accountId: AccountId,
                           by: AccountId,
                           notified: Boolean,
                           requestStatus: FriendRequestStatusType,
                           requestedAt: Long
                         )
