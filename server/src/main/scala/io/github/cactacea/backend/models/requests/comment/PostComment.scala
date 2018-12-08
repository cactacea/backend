package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.swagger.annotations.ApiModelProperty

case class PostComment(
                        @ApiModelProperty(value = "Feed Identifier.", required = true)
                        id: FeedId,

                        @ApiModelProperty(value = "A message will be posted.", required = true)
                        @Size(min = 1, max = 1000) message: String
                      )
