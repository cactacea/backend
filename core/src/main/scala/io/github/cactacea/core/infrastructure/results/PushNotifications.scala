package io.github.cactacea.core.infrastructure.results

import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class PushNotifications(
                   accountId: AccountId,
                   displayName: String,
                   token: String,
                   showContent: Boolean
                   )
