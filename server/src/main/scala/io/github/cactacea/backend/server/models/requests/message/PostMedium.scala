package io.github.cactacea.backend.server.models.requests.message

import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MediumId}
import io.swagger.annotations.ApiModelProperty

case class PostMedium(
                        @ApiModelProperty(value = "Group identifier.", required = true)
                        groupId: GroupId,

                        @ApiModelProperty(value = "A medium will be posted.", required = true)
                        mediumId: MediumId
                         )
