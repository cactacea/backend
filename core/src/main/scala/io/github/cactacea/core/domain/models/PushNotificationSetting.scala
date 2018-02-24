package io.github.cactacea.core.domain.models

case class PushNotificationSetting(
                                    followerFeed: Boolean,
                                    feedComment: Boolean,
                                    groupMessage: Boolean,
                                    directMessage: Boolean,
                                    groupInvite: Boolean,
                                    showMessage: Boolean
                               )
