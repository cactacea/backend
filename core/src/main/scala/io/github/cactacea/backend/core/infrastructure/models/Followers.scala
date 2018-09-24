package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FollowerId}

case class Followers(
                      id: FollowerId,
                      accountId: AccountId,
                      by: AccountId,
                      followedAt: Long
                        )
