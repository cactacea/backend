package io.github.cactacea.backend.server.models.requests.message

import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, MediumId}
import io.swagger.annotations.ApiModelProperty

case class PostMedium(
                       @ApiModelProperty(value = "Channel identifier.", required = true)
                        channelId: ChannelId,

                       @ApiModelProperty(value = "A medium will be posted.", required = true)
                        mediumId: MediumId
                         )
