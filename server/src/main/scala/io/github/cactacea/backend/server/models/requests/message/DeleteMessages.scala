package io.github.cactacea.backend.server.models.requests.message

import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class DeleteMessages (
                          @ApiModelProperty(value = "Group identifier.", required = true)
                          id: GroupId
                          )
