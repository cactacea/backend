package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.swagger.annotations.ApiModelProperty

case class PutChannel(
                      @ApiModelProperty(value = "Channel identifier.", required = true)
                      @RouteParam id: ChannelId,

                      @ApiModelProperty(value = "Channel name.", required = true)
                      @Size(min = 1, max = 1000) name: String,

                      @ApiModelProperty(value = "Only invited accounts can join in.", required = true)
                      byInvitationOnly: Boolean,

                      @ApiModelProperty(value = "Which accounts can join in.", required = true)
                      privacyType: ChannelPrivacyType,

                      @ApiModelProperty(value = "Which accounts can manage a channel.", required = true)
                      authorityType: ChannelAuthorityType
                     )
