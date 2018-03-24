package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class PushNotificationSettings(
                                     accountId: AccountId,
                                     groupInvitation: Boolean,
                                     followerFeed: Boolean,
                                     feedComment: Boolean,
                                     groupMessage: Boolean,
                                     directMessage: Boolean,
                                     showMessage: Boolean
                               )
