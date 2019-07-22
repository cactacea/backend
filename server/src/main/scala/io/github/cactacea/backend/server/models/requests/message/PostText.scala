package io.github.cactacea.backend.server.models.requests.message

import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class PostText(
                        @ApiModelProperty(value = "Group identifier.", required = true)
                        groupId: GroupId,

                        @ApiModelProperty(value = "A message will be posted.", required = true)
                        @Size(min = 1, max = 1000)  message: String

                         )
