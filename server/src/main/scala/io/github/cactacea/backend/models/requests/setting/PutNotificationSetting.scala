package io.github.cactacea.backend.models.requests.setting

import io.swagger.annotations.ApiModelProperty

case class PutNotificationSetting(
                                   @ApiModelProperty(value = "Notice follower feeds.")
                                   followerFeed: Boolean,

                                   @ApiModelProperty(value = "Notice feed comments or reply comments.")
                                   feedComment: Boolean,

                                   @ApiModelProperty(value = "Notice group messages.")
                                   groupMessage: Boolean,

                                   @ApiModelProperty(value = "Notice direct messages.")
                                   directMessage: Boolean,

                                   @ApiModelProperty(value = "Notice group invitations.")
                                   groupInvitation: Boolean,

                                   @ApiModelProperty(value = "Show messages on the push notification.")
                                   showMessage: Boolean
                                 )