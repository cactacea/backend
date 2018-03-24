package io.github.cactacea.backend.models.requests.message

import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MediumId}
import io.swagger.annotations.ApiModelProperty

case class PostMessage(
                        @ApiModelProperty(value = "Group identifier.")
                        id: GroupId,

                        @ApiModelProperty(value = "A message will be posted.")
                        @Size(min = 1, max = 1000)  message: Option[String],

                        @ApiModelProperty(value = "A medium will be posted.")
                        mediumId: Option[MediumId]
                         )
