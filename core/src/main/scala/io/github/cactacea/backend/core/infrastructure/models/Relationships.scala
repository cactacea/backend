package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId}

case class Relationships(
                          userId: UserId,
                          displayName: Option[String],
                          by: UserId,
                          follow: Boolean,
                          isFollower: Boolean,
                          muting: Boolean,
                          isFriend: Boolean,
                          friendRequestInProgress: Boolean,
                          followerBlockCount: Long,
                          followBlockCount: Long,
                          friendBlockCount: Long
                      )

