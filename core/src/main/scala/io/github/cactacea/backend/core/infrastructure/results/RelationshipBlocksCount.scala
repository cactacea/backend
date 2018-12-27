package io.github.cactacea.backend.core.infrastructure.results

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class RelationshipBlocksCount(
                                    id: AccountId,
                                    followerCount: Long,
                                    followingCount: Long,
                                    friendCount: Long
                                  )
