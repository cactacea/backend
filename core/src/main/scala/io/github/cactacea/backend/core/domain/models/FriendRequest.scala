package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId
import io.github.cactacea.backend.core.infrastructure.models.{Users, FriendRequests, Relationships}

case class FriendRequest (
                           id: FriendRequestId,
                           user: User,
                           requestedAt: Long,
                           next: Long
                         )

object FriendRequest {

  def apply(f: FriendRequests, a: Users, r: Option[Relationships], n: Long): FriendRequest = {
    val user = User(a, r)
    FriendRequest(
      id            = f.id,
      user       = user,
      requestedAt   = f.requestedAt,
      next          = n
    )
  }

}
