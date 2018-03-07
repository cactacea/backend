package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.core.infrastructure.identifiers.FriendRequestId
import io.github.cactacea.core.infrastructure.models.{Accounts, FriendRequests, Relationships}

case class FriendRequest (
                           id: FriendRequestId,
                           account: Account,
                           requestStatus: FriendRequestStatusType,
                           requestedAt: Long,
                           next: Long
                         )

object FriendRequest {

  def apply(f: FriendRequests, a: Accounts, r: Option[Relationships]): FriendRequest = {
    val account = Account(a, r)
    FriendRequest(
      id            = f.id,
      account          = account,
      requestStatus = f.requestStatus,
      requestedAt   = f.requestedAt,
      next          = f.requestedAt
    )
  }

}