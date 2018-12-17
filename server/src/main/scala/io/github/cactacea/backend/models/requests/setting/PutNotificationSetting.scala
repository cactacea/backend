package io.github.cactacea.backend.models.requests.setting

import io.swagger.annotations.ApiModelProperty

case class PutNotificationSetting(
                                   @ApiModelProperty(value = "Notice new follower feed arrived.", required = true)
                                   feed: Boolean,

                                   @ApiModelProperty(value = "Notice new comment arrived.", required = true)
                                   comment: Boolean,

                                   @ApiModelProperty(value = "Notice new friend request arrived.", required = true)
                                   friendRequest: Boolean,

                                   @ApiModelProperty(value = "Notice new message arrived.", required = true)
                                   message: Boolean,

                                   @ApiModelProperty(value = "Notice new group message arrived.", required = true)
                                   groupMessage: Boolean,

                                   @ApiModelProperty(value = "Notice new group invitation arrived.", required = true)
                                   groupInvitation: Boolean,

                                   @ApiModelProperty(value = "Show messages text on notification.", required = true)
                                   showMessage: Boolean
                                 )
