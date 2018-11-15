package io.github.cactacea.backend.models.requests.setting

import io.swagger.annotations.ApiModelProperty

case class PutNotificationSetting(
                                   @ApiModelProperty(value = "Notice follower feeds.", required = true)
                                   followerFeed: Boolean,

                                   @ApiModelProperty(value = "Notice feed comments or reply comments.", required = true)
                                   feedComment: Boolean,

                                   @ApiModelProperty(value = "Notice group messages.", required = true)
                                   groupMessage: Boolean,

                                   @ApiModelProperty(value = "Notice direct messages.", required = true)
                                   directMessage: Boolean,

                                   @ApiModelProperty(value = "Notice group invitations.", required = true)
                                   groupInvitation: Boolean,

                                   @ApiModelProperty(value = "Show messages on the push notification.", required = true)
                                   showMessage: Boolean
                                 )