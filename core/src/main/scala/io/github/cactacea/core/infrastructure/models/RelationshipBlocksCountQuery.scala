package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class RelationshipBlocksCountQuery(
                                    id: AccountId,
                                    followerCount: Long,
                                    followCount: Long,
                                    friendCount: Long
                                    )