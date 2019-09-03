package io.github.cactacea.backend.server.models.requests.comment

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Max
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.swagger.annotations.ApiModelProperty

case class GetCommentLikes(
                            @ApiModelProperty(value = "Comment Identifier.", required = true)
                            @RouteParam id: CommentId,

                            @ApiModelProperty(value = "Filters users which started on since or later.")
                            @QueryParam since: Option[Long],

                            @ApiModelProperty(value = "The offset of users. By default the value is 0.")
                            @QueryParam offset: Option[Int],

                            @ApiModelProperty(value = "Maximum number of users returned on one result page." +
                              " By default the value is 20 entries. The page size can never be larger than 50.")
                            @QueryParam @Max(50) count: Option[Int]
                           )

