package io.github.cactacea.core.infrastructure.results

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class RelationshipBlocksCount(
                                    id: AccountId,
                                    followerCount: Long,
                                    followCount: Long,
                                    friendCount: Long
                                    )