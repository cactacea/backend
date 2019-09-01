package io.github.cactacea.backend.server.models.requests.message

import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.swagger.annotations.ApiModelProperty

case class DeleteMessages (
                          @ApiModelProperty(value = "Channel identifier.", required = true)
                          id: ChannelId
                          )
