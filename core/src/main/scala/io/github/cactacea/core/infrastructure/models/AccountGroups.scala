package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{GroupId, AccountId}

case class AccountGroups(
                          accountId: AccountId,
                          groupId: GroupId,
                          unreadCount: Long,
                          hidden: Boolean,
                          mute: Boolean,
                          toAccountId: AccountId,
                          joinedAt: Long
                        )
