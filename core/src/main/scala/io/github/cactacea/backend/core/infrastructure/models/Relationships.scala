package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId}

case class Relationships(
                          accountId: AccountId,
                          displayName: Option[String],
                          by: AccountId,
                          follow: Boolean,
                          isFollower: Boolean,
                          muting: Boolean,
                          isFriend: Boolean,
                          friendRequestInProgress: Boolean,
                          followerBlockCount: Long,
                          followBlockCount: Long,
                          friendBlockCount: Long
                      )

