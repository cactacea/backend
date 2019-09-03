package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType}
import io.swagger.annotations.ApiModelProperty

case class PostChannel(
                       @ApiModelProperty(value = "Channel name.", required = true)
                       @Size(min = 1, max = 1000) name: String,

                       @ApiModelProperty(value = "Only invited users can join in.", required = true)
                       byInvitationOnly: Boolean,

                       @ApiModelProperty(value = "Which users can join in.", required = true)
                       privacyType: ChannelPrivacyType,

                       @ApiModelProperty(value = "Which users can manage a channel.", required = true)
                       authorityType: ChannelAuthorityType
                     )
