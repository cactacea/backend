package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId}

case class Relationships(
                          accountId: AccountId,
                          displayName: Option[String],
                          by: AccountId,
                          following: Boolean,
                          isFollower: Boolean,
                          muting: Boolean,
                          isFriend: Boolean,
                          friendRequestInProgress: Boolean,
                          followerBlockCount: Long,
                          followingBlockCount: Long,
                          friendBlockCount: Long
                      )

