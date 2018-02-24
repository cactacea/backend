package io.github.cactacea.core.infrastructure.queries

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class RelationshipBlocksCountQuery(
                                    id: AccountId,
                                    followerCount: Long,
                                    followCount: Long,
                                    friendCount: Long
                                    )