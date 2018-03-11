package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId}

case class Relationships(
                          accountId: AccountId,
                          editedDisplayName: Option[String],
                          by: AccountId,
                          follow: Boolean,
                          follower: Boolean,
                          mute: Boolean,
                          friend: Boolean,
                          inProgress: Boolean,
                          followedAt: Long,
                          mutedAt: Long,
                          friendedAt: Long
                      )
