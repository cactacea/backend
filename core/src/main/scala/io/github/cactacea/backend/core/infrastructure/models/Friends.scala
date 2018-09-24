package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendId}

case class Friends(
                    id: FriendId,
                    accountId: AccountId,
                    by: AccountId,
                    friendedAt: Long
                        )
