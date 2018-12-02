package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{FriendRequestId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, FriendRequests, Relationships}

case class FriendRequest (
                           id: FriendRequestId,
                           account: Account,
                           requestStatus: FriendRequestStatusType,
                           requestedAt: Long,
                           next: Option[Long]
                         )

object FriendRequest {

  def apply(f: FriendRequests, a: Accounts, r: Option[Relationships], n: Long): FriendRequest = {
    val account = Account(a, r)
    FriendRequest(
      id            = f.id,
      account       = account,
      requestStatus = f.requestStatus,
      requestedAt   = f.requestedAt,
      next          = Some(n)
    )
  }

}
