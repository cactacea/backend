package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.FriendRequest
import io.github.cactacea.core.infrastructure.models._

object FriendRequestFactory {

  def create(f: FriendRequests, u: Accounts, r: Option[Relationships]): FriendRequest = {
    val account = AccountFactory.create(u, r)
    FriendRequest(
      id            = f.id,
      account          = account,
      requestStatus = f.requestStatus,
      requestedAt   = f.requestedAt,
      next          = f.requestedAt
    )
  }

}
