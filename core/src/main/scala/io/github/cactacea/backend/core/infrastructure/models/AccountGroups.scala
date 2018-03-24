package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountGroupId, AccountId, GroupId}

case class AccountGroups(
                          id: AccountGroupId,
                          accountId: AccountId,
                          groupId: GroupId,
                          unreadCount: Long,
                          hidden: Boolean,
                          mute: Boolean,
                          toAccountId: AccountId,
                          joinedAt: Long
                        )
