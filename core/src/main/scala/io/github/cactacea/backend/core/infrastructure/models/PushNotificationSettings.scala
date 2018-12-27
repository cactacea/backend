package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class PushNotificationSettings(
                                     accountId: AccountId,
                                     feed: Boolean,
                                     comment: Boolean,
                                     friendRequest: Boolean,
                                     message: Boolean,
                                     groupMessage: Boolean,
                                     groupInvitation: Boolean,
                                     showMessage: Boolean
                               )
