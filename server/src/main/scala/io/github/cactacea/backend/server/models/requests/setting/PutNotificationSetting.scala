package io.github.cactacea.backend.server.models.requests.setting

import io.swagger.annotations.ApiModelProperty

case class PutNotificationSetting(
                                   @ApiModelProperty(value = "Notice new follower tweet arrived.", required = true)
                                   tweet: Boolean,

                                   @ApiModelProperty(value = "Notice new comment arrived.", required = true)
                                   comment: Boolean,

                                   @ApiModelProperty(value = "Notice new friend request arrived.", required = true)
                                   friendRequest: Boolean,

                                   @ApiModelProperty(value = "Notice new message arrived.", required = true)
                                   message: Boolean,

                                   @ApiModelProperty(value = "Notice new channel message arrived.", required = true)
                                   channelMessage: Boolean,

                                   @ApiModelProperty(value = "Notice new invitation arrived.", required = true)
                                   invitation: Boolean,

                                   @ApiModelProperty(value = "Show messages text on notification.", required = true)
                                   showMessage: Boolean
                                 )
