package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

case class PushNotificationSettings(
                                     userId: UserId,
                                     tweet: Boolean,
                                     comment: Boolean,
                                     friendRequest: Boolean,
                                     message: Boolean,
                                     channelMessage: Boolean,
                                     invitation: Boolean,
                                     showMessage: Boolean
                               )
