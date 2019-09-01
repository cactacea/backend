package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FollowerId}

case class Followers(
                      id: FollowerId,
                      userId: UserId,
                      by: UserId,
                      followedAt: Long
                        )
