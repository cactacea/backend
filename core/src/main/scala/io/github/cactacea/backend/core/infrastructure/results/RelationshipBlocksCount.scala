package io.github.cactacea.backend.core.infrastructure.results

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class RelationshipBlocksCount(
                                    id: AccountId,
                                    followerCount: Long,
                                    followCount: Long,
                                    friendCount: Long
                                  )