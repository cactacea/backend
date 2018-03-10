package io.github.cactacea.backend.models.requests.setting

case class PutNotificationSetting(
                                   followerFeed: Boolean,
                                   feedComment: Boolean,
                                   groupMessage: Boolean,
                                   directMessage: Boolean,
                                   groupInvitation: Boolean,
                                   showMessage: Boolean
                                 )