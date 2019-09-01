package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation._
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.swagger.annotations.ApiModelProperty


case class GetFollowers (
                          @ApiModelProperty(value = "User Identifier.", required = true)
                          @RouteParam id: UserId,

                          @ApiModelProperty(value = "Filters users whose user name start of.")
                          @QueryParam @Size(min = 0, max = 1000) userName: Option[String],

                          @ApiModelProperty(value = "Filters followers which started on since or later.")
                          @QueryParam since: Option[Long],

                          @ApiModelProperty(value = "The offset of followers. By default the value is 0.")
                          @QueryParam offset: Option[Int],

                          @ApiModelProperty(value = "Maximum number of followers returned on one result page." +
                            " By default the value is 20 entries. The page size can never be larger than 50.")
                          @QueryParam @Max(50) count: Option[Int]
                        )
