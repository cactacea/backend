package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}

case class DeliveryGroupInvitation(
                                         accountId: AccountId,
                                         displayName: String,
                                         groupId: GroupId,
                                         invitationdAt: Long,
                                         tokens: List[(String, AccountId)]
                                       )
