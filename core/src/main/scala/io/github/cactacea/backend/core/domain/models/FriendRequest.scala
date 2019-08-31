package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, FriendRequests, Relationships}

case class FriendRequest (
                           id: FriendRequestId,
                           account: Account,
                           requestedAt: Long,
                           next: Long
                         )

object FriendRequest {

  def apply(f: FriendRequests, a: Accounts, r: Option[Relationships], n: Long): FriendRequest = {
    val account = Account(a, r)
    FriendRequest(
      id            = f.id,
      account       = account,
      requestedAt   = f.requestedAt,
      next          = n
    )
  }

}
