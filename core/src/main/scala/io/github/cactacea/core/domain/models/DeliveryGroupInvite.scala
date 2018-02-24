package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}

case class DeliveryGroupInvite(
                                         accountId: AccountId,
                                         displayName: String,
                                         groupId: GroupId,
                                         invitedAt: Long,
                                         tokens: List[(String, AccountId)]
                                       )
